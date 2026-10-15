package com.example.qcollect.user;

import java.util.Locale;

public final class EmailAddress {

    private EmailAddress() {
    }

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }
}
