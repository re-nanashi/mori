package com.mori.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mori.shared.core.validator.PasswordMatchable;
import com.mori.shared.core.validator.PasswordMatches;
import com.mori.shared.core.validator.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class RegisterRequest implements PasswordMatchable {
    @NotBlank(message = "Email is required")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    @ValidEmail
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 30, message = "Username must be between 1 and 30 characters")
    @Pattern(
            regexp = "^(?![.])(?!.*\\.{2})[a-zA-Z0-9._]+(?<![.])$",
            message = "Username can only contain letters, numbers, periods, and underscores"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/]).{8,64}$",
            message = "Password must contain uppercase, lowercase, number, and a special character"
    )
    private String password;

    @JsonProperty("confirm_password")
    @NotBlank(message = "Confirmation password is required")
    private String confirmPassword;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
}