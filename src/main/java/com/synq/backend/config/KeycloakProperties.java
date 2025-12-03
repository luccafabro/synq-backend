package com.synq.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {

    private String clientId;
    private Admin admin = new Admin();
    private User user = new User();

    @Data
    public static class Admin {
        private String serverUrl;
        private String realm;
        private String username;
        private String password;
        private String clientId;
    }

    @Data
    public static class User {
        private String defaultPassword;
    }
}

