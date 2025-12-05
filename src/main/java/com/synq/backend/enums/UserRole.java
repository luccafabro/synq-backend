package com.synq.backend.enums;

/**
 * User role enum for authorization
 */
public enum UserRole {
    ADMIN("Administrator with full system access"),
    MANAGER("Manager with elevated privileges"),
    USER("Standard user with basic access");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



