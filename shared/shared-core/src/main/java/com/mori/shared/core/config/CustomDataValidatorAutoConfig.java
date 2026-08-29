package com.mori.shared.core.config;

import com.mori.shared.core.validator.EmailChecker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "shared.core.custom-data-validator.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class CustomDataValidatorAutoConfig {
    @Bean
    public EmailChecker emailChecker() {
        return new EmailChecker();
    }
}