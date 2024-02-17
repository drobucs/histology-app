package com.drobucs.histology.users.network;

import org.jetbrains.annotations.NotNull;

public class NetworkBotService {
    private static final String HOST = "http://172.17.0.1";
    private static final long PORT = 8091;
    private static final String API = "/api/histoexambot";
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
    public static String queryNewUser() {
        String x = api() + "/new-user";
        System.out.println("queryNewUser: '" + x + "'");
        return x;
    }
}
