package com.drobucs.histology.users.auth.controllers;

import com.drobucs.base.web.request.RequestPost;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.users.auth.models.User;
import com.drobucs.histology.users.auth.network.Network;
import com.drobucs.histology.users.auth.services.UserService;
import com.drobucs.web.apps.histology.users.enums.results.MoveToDBResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users/auth/1/confirm")
@RequiredArgsConstructor
public class ConfirmationController extends Api {
    private final UserService userService;
    @Value("${backend.key}")
    private String apiKey;
    @GetMapping("/{login}/{confirmationCode}")
    private String confirmEmail(@PathVariable String confirmationCode,
                                @PathVariable String login,
                                Model model) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            logError("confirmEmail: User is null.");
            model.addAttribute("error", true);
            return "confirmInfo";
        }
        if (!user.getConfirmationCode().equals(confirmationCode)) {
            logError("confirmEmail: Invalid confirmation code.");
            model.addAttribute("error", true);
            return "confirmInfo";
        }
        MoveToDBResult moveRes = moveUserToDB(user);
        if (moveRes != MoveToDBResult.OK) {
            logError("confirmEmail: Cannot move user to DB:MoveToDBResult=" + moveRes);
            model.addAttribute("error", true);
            return "confirmInfo";
        }
        userService.deleteUser(user);
        model.addAttribute("error", false);
        return "confirmInfo";
    }

    private MoveToDBResult moveUserToDB(User user) {
        RequestPost requestPost = new RequestPost(Network.queryAddUserToDB(apiKey));
        requestPost.setParams("login", user.getLogin())
                .setParams("email", user.getEmail())
                .setParams("phoneNumber", user.getPhoneNumber())
                .setParams("passwordSha", user.getPasswordSha());
        RequestResult<MoveToDBResult> result = requestPost.executeMoveToDBResult();
        if (result.haveErrors()) {
            logWarn("MoveUserToDB:Result=[" + result + "]");
        }
        return result.getResult();
    }
}
