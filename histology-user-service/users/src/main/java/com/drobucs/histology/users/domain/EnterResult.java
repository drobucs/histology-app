package com.drobucs.histology.users.domain;

import com.drobucs.histology.users.models.ClientUser;
import lombok.NonNull;

public class EnterResult {
    private final int code;
    private final ClientUser user;
    private final String message;

    public EnterResult(@NonNull ClientUser user) {
       this(0, user, "ok.");
    }

    public EnterResult(int code, @NonNull String message) {
        this(code, null, message);
    }

    public EnterResult(int code, ClientUser user, String message) {
        this.code = code;
        this.user = user;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public ClientUser getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
