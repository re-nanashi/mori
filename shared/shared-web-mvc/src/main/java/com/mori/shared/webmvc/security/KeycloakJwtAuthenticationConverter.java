package com.mori.shared.webmvc.security;

import com.mori.shared.core.security.KeycloakAuthorities;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        final Collection<GrantedAuthority> authorities = KeycloakAuthorities.from(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }
}