package com.synq.backend.service;

public interface KeycloakService {

    String createUser(String username, String email, String firstName, String lastName);

    void updateUser(String keycloakUserId, String email, String firstName, String lastName);

    void deleteUser(String keycloakUserId);

    void setUserEnabled(String keycloakUserId, boolean enabled);
}

