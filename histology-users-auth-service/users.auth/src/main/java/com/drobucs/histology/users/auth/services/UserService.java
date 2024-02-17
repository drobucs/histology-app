package com.drobucs.histology.users.auth.services;

import com.drobucs.base.web.request.RequestPost;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.users.auth.models.User;
import com.drobucs.histology.users.auth.network.Network;
import com.drobucs.histology.users.auth.repositories.UserRepository;
import com.drobucs.web.apps.histology.users.RegisterResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${backend.key}")
    private String apiKey;

    public RegisterResult register(@NonNull String login,
                                   @NonNull String email,
                                   @NonNull String phoneNumber,
                                   @NonNull String passwordSha) {
        RegisterResult validationRes = validateUser(login, email, phoneNumber);
        if (validationRes != RegisterResult.OK) {
            logger.error("UserService:Register:ValidationError:" + validationRes);
            return validationRes;
        }
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordSha(passwordSha);
        user.setConfirmationCode(generateConfirmationCode());
        userRepository.save(user);
        return RegisterResult.OK;
    }

    private String generateConfirmationCode() {
        SecureRandom secureRandom = new SecureRandom();
        long length = 10 + secureRandom.nextLong(64);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(secureRandom.nextLong(10));
        }
        return sb.toString();
    }

    @NotNull
    private RegisterResult validateUser(@NonNull String login,
                                        @NonNull String email,
                                        @NonNull String phoneNumber) {
        RequestPost request = new RequestPost(Network.queryUserValidation(apiKey));
        request.setParams("login", login);
        request.setParams("email", email);
        request.setParams("phoneNumber", phoneNumber);
        RequestResult<RegisterResult> res = request.executeRegisterResult();
        if (res.getResult() == null) {
            logger.error("UserService:ValidateUser:Result=[" + res + "]");
            return RegisterResult.UNKNOWN_ERROR;
        }
        logger.info("UserService:ValidateUser:Result=[" + res + "]");
        return res.getResult();
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public void deleteUser(User user) {
       userRepository.delete(user);
    }
}
