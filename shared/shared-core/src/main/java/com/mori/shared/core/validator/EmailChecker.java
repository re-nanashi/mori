package com.mori.shared.core.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class EmailChecker {
    private final Set<String> DISPOSABLE_EMAIL_DOMAINS;

    public EmailChecker() {
        this.DISPOSABLE_EMAIL_DOMAINS = loadBlocklist();
        if (DISPOSABLE_EMAIL_DOMAINS.isEmpty()) {
            log.error("CRITICAL: Disposable email blocklist failed to load or is empty.");
            throw new IllegalStateException("Disposable email blocklist failed to load - refusing to start");
        } else {
            log.info("Loaded {} disposable email domains", DISPOSABLE_EMAIL_DOMAINS.size());
        }
    }

    private Set<String> loadBlocklist() {
        Set<String> domains = new HashSet<>();
        try (InputStream is = EmailChecker.class.getResourceAsStream("/disposable_email_blocklist.conf")) {
            if (is == null) {
                throw new IllegalStateException("disposable_email_blocklist.conf not found on classpath");
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        domains.add(line);
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Failed to load list of disposable email domains.", ex);
        }

        return domains;
    }

    public boolean isDisposable(String email) {
        String normalized = email.toLowerCase().trim();
        String domain = normalized.split("@")[1];

        return DISPOSABLE_EMAIL_DOMAINS.contains(domain);
    }
}