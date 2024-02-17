package com.drobucs.histology.users.auth.network;

import org.jetbrains.annotations.NotNull;

public class Network {
    private static final String HOST = "http://172.17.0.1";
    private static final long PORT = 8082;
    private static final String API = "/api/backend/users";
    private static final String API_VERSION = "1";

    @NotNull
    private static String generalHost() {
        return HOST;
    }

    @NotNull
    private static String host() {
        return generalHost() + ":" + PORT;
    }
    @NotNull
    private static String api(@NotNull String apiKey) {
        return host() + API + "/" + API_VERSION + "/" + apiKey;
    }

    public static String queryUserValidation(@NotNull String apiKey) {
        return api(apiKey) + "/validate/user";
    }

    public static String queryAddUserToDB(@NotNull String apiKey) {
        return api(apiKey) + "/add/user/to/db";
    }

}
