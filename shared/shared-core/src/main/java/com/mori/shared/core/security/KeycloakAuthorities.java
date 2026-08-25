package com.mori.shared.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class KeycloakAuthorities {
    private KeycloakAuthorities() {}

    public static Collection<GrantedAuthority> from(Jwt jwt) {
        if (jwt == null) {
            log.warn("Null JWT passed to authority extraction");
            return List.of();
        }

        // Collect realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            log.warn("No realm_access roles found in JWT");
            return List.of();
        }

        Object roles = realmAccess.get("roles");
        if (!(roles instanceof Collection<?> collection)) {
            log.warn("Expected roles to be a Collection, but got: {}", roles != null ? roles.getClass().getName() : "null");
            return List.of();
        }

        Collection<GrantedAuthority> realmRoles = collection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toUnmodifiableList());

        log.debug("Realm roles extracted: {}", realmRoles);
        log.info("Role conversion completed for {} roles", realmRoles.size());

        return realmRoles;
    }
}