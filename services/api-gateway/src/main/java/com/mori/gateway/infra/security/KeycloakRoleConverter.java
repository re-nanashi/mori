package com.mori.gateway.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Flux;

import java.util.*;

// TODO: Collect Client Roles for fine-grained authorization
@Slf4j
public class KeycloakRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        // Collect realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            log.warn("No realm_access roles found in JWT");
            return Flux.empty();
        }

        List<String> realmRoles = (List<String>) realmAccess.get("roles");
        log.debug("Realm roles extracted: {}", realmRoles);
        log.info("Role conversion completed for {} roles", realmRoles.size());

        return Flux.fromIterable(realmRoles)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}