package com.drobucs.histology.users.controllers;

import com.drobucs.base.web.request.RequestGet;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.users.domain.EnterResult;
import com.drobucs.histology.users.domain.Time;
import com.drobucs.histology.users.models.ClientUser;
import com.drobucs.histology.users.models.User;
import com.drobucs.histology.users.network.NetworkUsersAuthService;
import com.drobucs.histology.users.services.StatService;
import com.drobucs.histology.users.services.UserService;
import com.drobucs.web.apps.histology.users.Privilege;
import com.drobucs.web.apps.histology.users.enums.results.BooleanResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Date;


@RestController
@RequestMapping("/api/users/1")
@RequiredArgsConstructor
public class APIController extends Api {
    private final UserService userService;
    private final StatService statService;
    private final JavaMailSender mailSender;
    @Value("histoexam@yandex.ru")
    private String emailFrom;

    @Value("${backend.key.users.auth}")
    private String backendApiKey;

    @GetMapping("/test")
    public String test() {
        return "API is alive.";
    }

    /* Returns:
        code=0 - ok.
        code=1 - no user with this login.
        code=2 - incorrect password.
        code=3 - user is disabled.
        code=4 - user doesn't confirm email */
    @PostMapping("/signIn")
    public synchronized EnterResult signIn(@RequestParam("login") String login,
                                           @RequestParam("passwordSha") String passwordSha) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            if (userIsInTemporaryUsers(login)) {
                statService.enter(-1L, "EMAIL_NOT_CONFIRMED");
                return new EnterResult(4, "User doesn't confirm email.");
            }
            statService.enter(-1L, "INVALID_LOGIN");
            return new EnterResult(1, "No user with this login.");
        }
        if (user.getApiKeySha512() == null) {
            user = userService.generateApiKey(user);
        }
        if (!user.getPasswordSha().equals(passwordSha)) {
            statService.enter(user.getId(), "INVALID_PASSWORD");
            return new EnterResult(2, "Incorrect password.");
        }
        if (!user.isEnabled()) {
            statService.enter(user.getId(), "USER_IS_DISABLED");
            return new EnterResult(3, "User is disabled.");
        }
        statService.enter(user.getId(), "SUCCESS");
        return new EnterResult(new ClientUser(user));
    }

    private boolean userIsInTemporaryUsers(@NotNull String login) {
        RequestGet requestGet = new RequestGet(NetworkUsersAuthService.queryUserExist(backendApiKey, login));
        RequestResult<BooleanResult> result = requestGet.executeBooleanResult();
        if (result.haveErrors()) {
            return false;
        }
        return result.getResult() == BooleanResult.TRUE;
    }

    /* Returns:
        code=0 - ok.
        code=1 - no user with this login.
        code=2 - users email incorrect. */
    @GetMapping("check/users/email/{login}/{email}")
    public int checkUsersEmail(@PathVariable String login, @PathVariable String email) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            logInfo("User is null.");
            return 1;
        }
        if (!user.getEmail().equals(email)) {
            logInfo("Not equals: '" + user.getEmail() + "' != '" + email + "'.");
            return 2;
        }
        return 0;
    }

    /* Returns:
        code=0 - ok.
        code=1 - no user with this login.
        code=2 - users email incorrect.
        code=3 - mail exception. */
    @GetMapping("send/secret/code/{login}/{email}")
    public synchronized int sendSecretCode(@PathVariable String login, @PathVariable String email) {
        int check = checkUsersEmail(login, email);
        if (check != 0) {
            return check;
        }
        User user = userService.getUserByLogin(login);
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(emailFrom);
        simpleMessage.setTo(email);
        simpleMessage.setSubject("Recovery password");
        String code = generateCode();
        user.setRecoveryCode(code);
        userService.saveUser(user);
        simpleMessage.setText("Your code: " + code);
        try {
            mailSender.send(simpleMessage);
        } catch (MailException e) {
            user.setRecoveryCode("null");
            userService.saveUser(user);
            return 3;
        }
        return 0;
    }

    /* Returns:
       code=0 - ok.
       code=1 - no user with this login.
       code=2 - incorrect code. */
    @PostMapping("/check/secret/code/{login}")
    public synchronized int checkSecretCode(@RequestParam("code") String code, @PathVariable String login) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return 1;
        }
        if (!user.getRecoveryCode().equals(code)) {
            return 2;
        }
        user.setRecoveryCode("null");
        userService.saveUser(user);
        return 0;
    }

    /* Returns:
       code=0 - ok.
       code=1 - no user with this login. */
    @PostMapping("/recovery/password/{login}")
    public synchronized int recoveryPassword(@PathVariable String login, @RequestParam("newPasswordSha") String newPasswordSha) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return 1;
        }
        user.setPasswordSha(newPasswordSha);
        userService.saveUser(user);
        return 0;
    }

    /* Returns:
       code=0 - ok.
       code=1 - no user with this login.
       code=2 - user not confirmed email*/
    @GetMapping("/check/users/existence/{login}")
    public int checkUsersExistence(@PathVariable String login) {
        if (userService.loginIsExist(login)) {
            return 0;
        }
        if (userIsInTemporaryUsers(login)) {
            return 2;
        }
        return 1;
    }

    /* Result:
        code=0 - ok
        code=1 - email exist*/
    @GetMapping(value = "/check/email/unique/{email}")
    public int checkEmailUnique(@PathVariable String email) {
        if (userService.emailIsExist(email)) {
            return 1;
        }
        return 0;
    }

    @GetMapping("/user/have/subscription/{login}/{apiKey}")
    private synchronized int userHaveSubscription(@PathVariable String apiKey, @PathVariable String login) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return 3;
        }
        if (isBadApiKey(user, apiKey)) {
            return 2;
        }
        updateUserPrivilege(user);
        if (user.getPrivileges() == Privilege.DEFAULT) {
            return 1;
        }
        return 0;
    }

    @GetMapping("/user/update/{login}/{apiKey}")
    public ClientUser updateUser(@PathVariable String apiKey, @PathVariable String login) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return null;
        }
        if (isBadApiKey(user, apiKey)) {
            return null;
        }
        updateUserPrivilege(user);
        return new ClientUser(user);
    }

    @GetMapping("/get/common/user")
    public ClientUser getCommonUser() {
        User user = userService.getUserByLogin("___common___");
        if (user == null) {
            return null;
        }
        return new ClientUser(user);
    }

    private synchronized boolean isBadApiKey(@NotNull User user, @NotNull String apiKey) {
        if (user.getApiKeySha512() == null) {
            userService.generateApiKey(user);
            userService.saveUser(user);
        }
        return user.getApiKeySha512() == null || !user.getApiKeySha512().equals(apiKey);
    }

    @NonNull
    private synchronized String generateCode() {
        SecureRandom rand = new SecureRandom();
        rand.setSeed(23);
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; ++i) {
            code.append(rand.nextLong(10));
        }
        return code.toString();
    }

    private synchronized void updateUserPrivilege(@NotNull User user) {
        if (new Date().getTime() - user.getPrivilegeExpires().getTime() > 0) {
            user.setPrivileges(Privilege.DEFAULT);
            userService.saveUser(user);
        }
    }
}
