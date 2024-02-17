package com.drobucs.histology.users.services;

import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class DataAlreadyExistException extends Exception {
    public DataAlreadyExistException(@Nullable Object msg) {
        super(msg == null ? "null" : msg.toString());
    }

    public static DataAlreadyExistException getException(@NotNull String name, @Nullable String data) {
        return new DataAlreadyExistException(name + " '"  + data + "' already exist.");
    }
}
