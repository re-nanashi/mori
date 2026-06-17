package com.mori.gateway.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class TestController {
    @GetMapping("/public")
    public Mono<String> testPublic() {
        return Mono.just("This is a public endpoint.");
    }

    @GetMapping("/private")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> testPrivate() {
        return Mono.just("This is a private endpoint.");
    }
}