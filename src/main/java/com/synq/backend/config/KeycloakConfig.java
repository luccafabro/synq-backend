package com.synq.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Keycloak Admin Client
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class KeycloakConfig {

    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloakAdminClient() {
        log.info("Initializing Keycloak Admin Client for realm: {}", 
                keycloakProperties.getAdmin().getRealm());
        
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAdmin().getServerUrl())
                .realm(keycloakProperties.getAdmin().getRealm())
                .username(keycloakProperties.getAdmin().getUsername())
                .password(keycloakProperties.getAdmin().getPassword())
                .clientId(keycloakProperties.getAdmin().getClientId())
                .build();
    }
}

