package com.synq.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test configuration with Testcontainers MySQL
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    protected static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("synq_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    static {
        mysqlContainer.start();
        System.setProperty("SYNC_DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("SYNC_DB_USER", mysqlContainer.getUsername());
        System.setProperty("SYNC_DB_PASSWORD", mysqlContainer.getPassword());
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads successfully
    }
}

