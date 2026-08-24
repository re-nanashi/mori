package com.mori.shared.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof PasswordMatchable request) {
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();

            // Let @NotBlank handle the error if missing
            boolean passwordMissing =  password == null || password.isBlank();
            boolean confirmPasswordMissing = confirmPassword == null || confirmPassword.isBlank();

            if (passwordMissing || confirmPasswordMissing) {
                return true;
            }

            boolean matches = request.getPassword()
                    .equals(request.getConfirmPassword());

            if (!matches) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                context.getDefaultConstraintMessageTemplate()
                        )
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
            }

            return matches;
        }

        return false;
    }
}