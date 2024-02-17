package com.drobucs.histology.users.services;

import com.drobucs.base.time.Time;
import com.drobucs.histology.users.encryption.EncryptionInfo;
import com.drobucs.histology.users.encryption.Random;
import com.drobucs.histology.users.models.User;
import com.drobucs.histology.users.repositories.UserRepository;
import com.drobucs.web.apps.histology.users.Privilege;
import com.google.common.hash.Hashing;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

import static com.drobucs.histology.users.encryption.EncryptionInfo.salt;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }

    public User getUserByLogin(@NonNull String login) {
        return userRepository.getUserByLogin(login);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void log(@Nullable Object msg) {
        logger.info(Objects.toString(msg));
    }

    @NonNull
    public User saveUser(@NonNull String login,
                         @NonNull String passwordSha,
                         @NonNull String email) throws NoSuchAlgorithmException, DataAlreadyExistException {

        return saveUser(login, passwordSha, email, "null");
    }
    public User saveUser(@NonNull String login,
                         @NonNull String passwordSha,
                         @NonNull String email,
                         @NonNull String phoneNumber) throws NoSuchAlgorithmException, DataAlreadyExistException {
        checkLoginUnique(login);
        checkEmailUnique(email);
        User user = new User();
        initUser(user, login, passwordSha, email, phoneNumber);
        user = userRepository.save(user);
        return user;
    }

    private void initUser(User user, String login, String passwordSha, String email, String phoneNumber)
            throws NoSuchAlgorithmException {
        user.setLogin(login);
        user.setPasswordSha(passwordSha);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPrivilegeExpires(new Date());
        user.setEnabled(true);
        user.setEncyptionKey(genrateEncryptionKey());
        user.setPrivileges(Privilege.DEFAULT);
        user.setRecoveryCode("null");
        user.setApiKeySha512(generateApiKeySha512(user));
    }

    private String generateApiKeySha512(User user) {
        SecureRandom secureRandom = Random.getRandom();
        String strToEnc = secureRandom.nextLong() +
                salt(user.getLogin()) +
                secureRandom.nextLong() +
                salt(user.getEmail()) +
                secureRandom.nextLong() +
                salt(user.getPasswordSha()) +
                secureRandom.nextLong();
        return Hashing.sha512().hashString(strToEnc, StandardCharsets.UTF_8).toString();
    }

    private Key genrateEncryptionKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = EncryptionInfo.getInstance().getKeyGenerator();
        keyGen.init(new SecureRandom());
        return keyGen.generateKey();
    }

    /* Result:
        code=0 - ok
        code=1 - email exist*/
    public boolean emailIsExist(@NotNull String email) {
        return userRepository.getUSerByEmail(email) != null;
    }
    private void checkEmailUnique(@NotNull String email) throws DataAlreadyExistException {
        if (emailIsExist(email)) {
            throw DataAlreadyExistException.getException("Email", email);
        }
    }
    public boolean loginIsExist(@NotNull String login) {
        return userRepository.getUserByLogin(login) != null;
    }

    private void checkLoginUnique(@NotNull String login) throws DataAlreadyExistException {
        if (loginIsExist(login)) {
            throw DataAlreadyExistException.getException("Login", login);
        }
    }

    public User getUserByApiKey(String apiKey) {
        return userRepository.getUserByApiKeySha512(apiKey);
    }
    public boolean apiKeyExist(@NonNull String apiKey) {
        return getUserByApiKey(apiKey) != null;
    }

    public boolean havePrivilegesTo(String apiKey, Privilege privilegeContent) {
        User user = getUserByApiKey(apiKey);
        if (user == null) {
            return false;
        }
        return Privilege.havePrivilege(privilegeContent, user.getPrivileges());
    }

    public User generateApiKey(User user) {
        user.setApiKeySha512(generateApiKeySha512(user));
        return userRepository.save(user);
    }

    public long countUsers() {
        return userRepository.findAll().size();
    }

    public int issueSubscription(@NotNull User user, long month) {
        user.setPrivileges(Privilege.SUBSCRIPTION);
        Date date = new Date();
        long currentTime = date.getTime();
        log("Current time=" + currentTime);
        log("Number of month=" + month);
        log("Time.MONTH=" + Time.MONTH);
        long prod = Time.MONTH * month;
        log("Time.MONTH * month=" + prod);
        log("Time.HOUR=" + Time.HOUR);
        long setsTime = currentTime + prod + Time.HOUR;
        log("Set time: " + setsTime);
        date.setTime(setsTime);
        user.setPrivilegeExpires(date);
        userRepository.save(user);
        return 0;
    }

    public boolean loginIsExistInTempUsers(String login) {
        return false;
    }
}
