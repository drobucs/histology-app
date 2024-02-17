package com.drobucs.histology.payment.network;

import org.jetbrains.annotations.NotNull;

public class NetworkUsersService {
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
    @NotNull
    public static String queryCheckUsersApiKey(@NotNull String backendApiKey,
                                        @NotNull String login,
                                        @NotNull String apiKey) {
        return api(backendApiKey)  + "/check/users/apiKey/" + login + "/" + apiKey;
    }

    public static String queryGetUserId(@NotNull String backendApiKey) {
        return api(backendApiKey) + "/user/id";
    }

    public static String queryIssueSubscriptionToUser(@NotNull String backendApiKey) {
        return api(backendApiKey) + "/issue-subscription";
    }
}
