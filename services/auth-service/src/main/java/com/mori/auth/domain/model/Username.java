package com.mori.auth.domain.model;

import com.mori.shared.core.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Username {
    private static final List<String> RESERVED_USERNAMES = List.of(
            "admin",
            "mori",
            "support",
            "help",
            "root",
            "system"
    );

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Username cannot be blank");
        }

        String normalized = value.trim();

        boolean containsInvalidCaseInsensitive = RESERVED_USERNAMES.stream()
                .anyMatch(normalized::contains);
        if (RESERVED_USERNAMES.contains(normalized) || containsInvalidCaseInsensitive) {
            throw new ValidationException("Username '" + normalized + "' is reserved or contains a reserved keyword");
        }

        return new Username(normalized);
    }
}