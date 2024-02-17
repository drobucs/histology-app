package com.drobucs.histology.users.auth.controllers;

import com.drobucs.histology.users.auth.models.User;
import com.drobucs.histology.users.auth.services.UserService;
import com.drobucs.web.apps.histology.users.RegisterResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/auth/1/")
@RequiredArgsConstructor
public class APIController extends Api {

    private final UserService userService;
    private final JavaMailSender mailSender;
    @Value("histoexam@yandex.ru")
    private String emailFrom;

    @Value("${backend.key}")
    private String apiKey;

    @GetMapping("/test")
    private String test() {
        return "API is alive.";
    }

    @PostMapping("/signUp")
    private RegisterResult signUp(@RequestParam("login") String login,
                                  @RequestParam("email") String email,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  @RequestParam("passwordSha") String passwordSha) {
        RegisterResult result = userService.register(login, email, phoneNumber, passwordSha);
        if (result.haveErrors()) {
            logError("signUp: Error:" + result);
            return result;
        }
        User user = userService.getUserByLogin(login);
        if (user == null) {
            logError("signUp: registered user is null; login=" + login +
                    ",email=" + email +
                    ",phoneNumber=" + phoneNumber +
                    ",passwordSha=" + passwordSha);
            return RegisterResult.UNKNOWN_ERROR;
        }
        sendConfirmationEmail(user);
        logInfo("signUp: Result should be \"OK\": result=\"" + result + "\"");
        return result;
    }

    private void sendConfirmationEmail(@NotNull User user) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(emailFrom);
        simpleMessage.setTo(user.getEmail());
        simpleMessage.setSubject("Подтверждение почты.");
        simpleMessage.setText(" If you have registered in the Histoexam application, follow the link below to confirm your email.\n" +
                " If it wasn't you, ignore this email\n" +
                "https://drobucs.ru/api/users/auth/1/confirm/" + user.getLogin() + "/" + user.getConfirmationCode());

        try {
            mailSender.send(simpleMessage);
        } catch (MailException e) {
            logError("sendConfirmationEmail(User): MailException:" + e.getMessage());
        }
    }


}
