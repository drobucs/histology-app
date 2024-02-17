package com.drobucs.histology.users.models;

import com.drobucs.web.apps.histology.users.Privilege;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ClientUser {
    private final String login;
    private final String email;
    private final Privilege privileges;
    private final boolean enable;
    private final String apiKey;

    public ClientUser(String login, String email, Privilege privileges, boolean enable, String apiKey) {
        this.login = login;
        this.email = email;
        this.privileges = privileges;
        this.enable = enable;
        this.apiKey = apiKey;
    }

    public ClientUser(@NotNull User user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.privileges = user.getPrivileges();
        this.enable = user.isEnabled();
        this.apiKey = user.getApiKeySha512();
    }

    public boolean isEnable() {
        return enable;
    }

}
