package com.synq.backend.config;

import com.synq.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Database initialization configuration
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final RoleService roleService;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Initializing database with default data...");
            roleService.initializeDefaultRoles();
            log.info("Database initialization completed");
        };
    }
}

