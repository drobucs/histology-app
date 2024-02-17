package com.drobucs.histology.users.auth.controllers;

import com.drobucs.histology.users.auth.services.BackendApiKeyService;
import com.drobucs.histology.users.auth.services.UserService;
import com.drobucs.web.apps.histology.users.enums.results.BooleanResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backend/users/auth/1/{apiKey}")
public class APIBackendController extends Api {
    private final UserService userService;
    private final BackendApiKeyService backendApiKeyService;

    @GetMapping("/user/exist/{login}")
    private BooleanResult userExist(@PathVariable String apiKey, @PathVariable String login) {
        if (isBadApiKey(apiKey)) {
            return BooleanResult.INVALID_API_KEY;
        }
        if (userService.getUserByLogin(login) == null) {
            return BooleanResult.FALSE;
        }
        return BooleanResult.TRUE;
    }

    private boolean isBadApiKey(String apiKey) {
        String key = backendApiKeyService.getKeyByName("common");
        if (key == null || apiKey == null) {
            logError("isBadApiKey: key=" + key + ", apiKey=" + apiKey);
            return true;
        }
        if (!key.equals(apiKey)) {
            logWarn("isBadApiKey: key='" + key + "' != '" + apiKey + "'=apiKey.");
            return true;
        }
        return false;
    }
}
