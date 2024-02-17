package com.drobucs.histology.users.network;

import org.jetbrains.annotations.NotNull;

public class NetworkStatService {
    private static final String HOST = "http://172.17.0.1";
    private static final long PORT = 8090;
    private static final String API = "/api/backend/stat";
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
    private static String api() {
        return host() + API + "/" + API_VERSION;
    }
    @NotNull
    public static String queryEventEnter() {
        return api() + "/event/enter";
    }

    @NotNull
    public static String queryEventRegister() {
        return api() + "/event/register";
    }
}
