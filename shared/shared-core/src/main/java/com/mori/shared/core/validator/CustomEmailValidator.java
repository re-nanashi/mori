package com.mori.shared.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class CustomEmailValidator implements ConstraintValidator<ValidEmail, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    );
    private final EmailChecker emailChecker;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // let @NotBlank handle validation if missing
        if (email == null || email.isBlank()) {
            return true;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            context.disableDefaultConstraintViolation(); // suppress default message
            context.buildConstraintViolationWithTemplate("Invalid email format")
                    .addConstraintViolation();
            return false;
        }

        if (emailChecker.isDisposable(email)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid email domain")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}