package com.mori.auth.domain.model;

import com.mori.shared.core.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Email {
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Email cannot be blank");
        }

        String normalized = value.toLowerCase().trim();

        return new Email(normalized);
    }
}