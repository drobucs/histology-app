package com.drobucs.histology.users.controllers;

import com.drobucs.histology.users.models.User;
import com.drobucs.histology.users.services.BackendKeyService;
import com.drobucs.histology.users.services.DataAlreadyExistException;
import com.drobucs.histology.users.services.StatService;
import com.drobucs.histology.users.services.UserService;
import com.drobucs.web.apps.histology.users.Privilege;
import com.drobucs.web.apps.histology.users.RegisterResult;
import com.drobucs.web.apps.histology.users.enums.results.MoveToDBResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backend/users/1/{backendApiKey}")
public class APIBackendController extends Api {

    private final BackendKeyService backendKeyService;

    private final UserService userService;

    private final StatService statService;

    @GetMapping("/test")
    public String test(@PathVariable String backendApiKey) {
        // apiKey ignored
        return "API is alive. Version 1.";
    }

    /*  Result:
            code=0 - user api key exist
            code=1 - incorrect backend key
            code=2 - users api key not exist
    */
    @GetMapping("/check/users/apiKey/{apiKey}")
    public int checkUsersApiKey(@PathVariable String apiKey, @PathVariable String backendApiKey) {
        if (isBadBackendKey(backendApiKey)) {
            return 1;
        }
        if (userService.apiKeyExist(apiKey)) {
            return 0;
        }
        return 2;
    }
    /*  Result:
            code=0 - ok. login match with apiKey
            code=1 - incorrect backend key
            code=2 - invalid login. No such login.
            code=3 - invalid api key.
    */
    @GetMapping("/check/users/apiKey/{login}/{apiKey}")
    public int checkUsersApiKey(@PathVariable String apiKey,
                                @PathVariable String backendApiKey,
                                @PathVariable String login) {
        if (isBadBackendKey(backendApiKey)) {
            return 1;
        }
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return 2;
        }
        if (!apiKey.equals(user.getApiKeySha512())) {
            return 3;
        }
        return 0;
    }


    /*  Result:
            code=0 - user have privilege
            code=1 - incorrect backend key
            code=2 - user haven't privilege
    */
    @PostMapping("have/privileges/{apiKey}")
    public int havePrivileges(@PathVariable String apiKey,
                              @PathVariable String backendApiKey,
                              @RequestParam("privilegeContent") Privilege privilegeContent) {
        if (isBadBackendKey(backendApiKey)) {
            return 1;
        }
        if (userService.havePrivilegesTo(apiKey, privilegeContent)) {
            return 0;
        }
        return 2;
    }

    @PostMapping("/add/user/to/db")
    public MoveToDBResult moveUserToDB(@PathVariable String backendApiKey,
                                       @RequestParam("login") String login,
                                       @RequestParam("email") String email,
                                       @RequestParam("phoneNumber") String phoneNumber,
                                       @RequestParam("passwordSha") String passwordSha) {
        if (isBadBackendKey(backendApiKey)) {
            return MoveToDBResult.INVALID_API_KEY;
        }
        try {
            userService.saveUser(login, passwordSha, email, phoneNumber);
            statService.register(login);
        } catch (NoSuchAlgorithmException e) {
            logError("moveUserToDB: NoSuchAlgorithmException:" + e.getMessage());
            return MoveToDBResult.NO_SUCH_ALGORITHM_EXCEPTION;
        } catch (DataAlreadyExistException e) {
            logError("moveUserToDB: DataAlreadyExistException:" + e.getMessage());
            return MoveToDBResult.DATA_ALREADY_EXIST_EXCEPTION;
        }
        return MoveToDBResult.OK;
    }

    @PostMapping("/validate/user")
    public RegisterResult validateUser(@PathVariable String backendApiKey,
                                       @RequestParam("login") String login,
                                       @RequestParam("email") String email,
                                       @RequestParam("phoneNumber") String phoneNumber) {
        if (isBadBackendKey(backendApiKey)) {
            logInfo("validateUser:Result:" + RegisterResult.INVALID_API_KEY);
            return RegisterResult.INVALID_API_KEY;
        }
        if (userService.loginIsExist(login)) {
            logInfo("validateUser:Result:" + RegisterResult.LOGIN_ALREADY_EXIST_ERROR);
            return RegisterResult.LOGIN_ALREADY_EXIST_ERROR;
        }
        if (userService.emailIsExist(email)) {
            logInfo("validateUser:Result:" + RegisterResult.EMAIL_ALREADY_EXIST_ERROR);
            return RegisterResult.EMAIL_ALREADY_EXIST_ERROR;
        }
        // phoneNumber check is not implemented.
        return RegisterResult.OK;
    }

    private boolean isBadBackendKey(@Nullable String key) {
        if (key == null) {
            return true;
        }
        String actualKey = backendKeyService.getKeyByName("general");
        logInfo("isBadBackendKey: actualKey=" + actualKey + "; key=" + key);
        logInfo("isBadBackendKey result: " + !key.equals(actualKey));
        return !key.equals(actualKey);
    }

    @PostMapping("/user/id")
    private Long getUserId(@PathVariable String backendApiKey,
                           @RequestParam("login") String login) {
        if (isBadBackendKey(backendApiKey)) {
            return -1L;
        }
        if (login == null) {
            return -2L;
        }
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return -3L;
        }
        return user.getId();
    }

    @PostMapping("/user/is/enable")
    private int userIsEnabled(@PathVariable String backendApiKey,
                              @RequestParam("apiKey") String apiKey) {
        if (isBadBackendKey(backendApiKey)) {
            return 1;
        }
        if (apiKey == null) {
            return 2;
        }
        User user = userService.getUserByApiKey(apiKey);
        if (user == null) {
            return 3;
        }
        if (!user.isEnabled()) {
            return 4;
        }
        return 0;
    }

    @GetMapping("/users/count")
    private long usersCount(@PathVariable String backendApiKey) {
        if (isBadBackendKey(backendApiKey)) {
            return -1;
        }
        return userService.countUsers();
    }

    /*
      res=0 - ok
    * res=1 - bad apiKey
    * res=2 - no user with such id
      res=3 - month is null.
    * */
    @PostMapping("/issue-subscription")
    private int issueSubscription(@PathVariable String backendApiKey,
                                  @RequestParam("userId") Long userId,
                                  @RequestParam("month") Long month) {
        if (isBadBackendKey(backendApiKey)) {
            logInfo("Bad backend api key.");
            return 1;
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            logError("Cannot find user with such id='" + userId + "'.");
            return 2;
        }
        if (month == null) {
            logError("Month is null.");
            return 3;
        }
        return userService.issueSubscription(user, month);
    }

    @PostMapping("/get-id-by-apiKey")
    private long getIdByApiKey(@PathVariable String backendApiKey,
                               @RequestParam("apiKey") String apiKey) {
        if (isBadBackendKey(backendApiKey)) {
            logInfo("Bad backend api key.");
            return -1;
        }
        User user = userService.getUserByApiKey(apiKey);
        if (user == null) {
            logInfo("No user with such apiKey='" + apiKey + "'.");
            return -2;
        }
        return user.getId();
    }
}
