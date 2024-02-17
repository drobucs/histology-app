package com.drobucs.histology.users.services;

import jakarta.annotation.Nullable;

public class SameLoginException extends DataAlreadyExistException {
    public SameLoginException(@Nullable Object msg) {
        super(msg);
    }
}
