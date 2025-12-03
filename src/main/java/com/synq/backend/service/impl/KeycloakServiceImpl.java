package com.synq.backend.service.impl;

import com.synq.backend.config.KeycloakProperties;
import com.synq.backend.exceptions.EndpointException;
import com.synq.backend.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakProperties keycloakProperties;

    @Override
    public String createUser(String username, String email, String firstName, String lastName) {
        log.debug("Creating user in Keycloak: {}", username);

        try {
            RealmResource realmResource = getRealmResource();
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> existingUsers = usersResource.search(username, true);
            if (!existingUsers.isEmpty()) {
                log.warn("User already exists in Keycloak: {}", username);
                return existingUsers.get(0).getId();
            }

            var userRepresentation = createUserRepresentation(username, email, firstName, lastName);

            Response response = usersResource.create(userRepresentation);

            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Failed to create user in Keycloak. Status: {}, Error: {}", 
                        response.getStatus(), errorMessage);
                throw new EndpointException("Failed to create user in Keycloak: " + errorMessage, 
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String locationPath = response.getLocation().getPath();
            String keycloakUserId = locationPath.substring(locationPath.lastIndexOf('/') + 1);

            log.info("User created in Keycloak with ID: {}", keycloakUserId);

            setDefaultPassword(keycloakUserId);

            response.close();

            return keycloakUserId;

        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", username, e);
            throw new EndpointException("Error creating user in Keycloak: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(String keycloakUserId, String email, String firstName, String lastName) {
        log.debug("Updating user in Keycloak: {}", keycloakUserId);

        try {
            UserResource userResource = getUserResource(keycloakUserId);
            UserRepresentation user = userResource.toRepresentation();

            if (email != null) {
                user.setEmail(email);
            }

            if (firstName != null) {
                user.setFirstName(firstName);
            }

            if (lastName != null) {
                user.setLastName(lastName);
            }

            userResource.update(user);
            log.info("User updated in Keycloak: {}", keycloakUserId);

        } catch (Exception e) {
            log.error("Error updating user in Keycloak: {}", keycloakUserId, e);
            throw new EndpointException("Error updating user in Keycloak: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteUser(String keycloakUserId) {
        log.debug("Deleting user from Keycloak: {}", keycloakUserId);

        try {
            getUserResource(keycloakUserId).remove();
            log.info("User deleted from Keycloak: {}", keycloakUserId);

        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", keycloakUserId, e);
            throw new EndpointException("Error deleting user from Keycloak: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void setUserEnabled(String keycloakUserId, boolean enabled) {
        log.debug("Setting user enabled status in Keycloak: {} -> {}", keycloakUserId, enabled);

        try {
            UserResource userResource = getUserResource(keycloakUserId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);

            log.info("User enabled status updated in Keycloak: {} -> {}", keycloakUserId, enabled);

        } catch (Exception e) {
            log.error("Error updating user enabled status in Keycloak: {}", keycloakUserId, e);
            throw new EndpointException("Error updating user in Keycloak: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserRepresentation createUserRepresentation(String username, String email, String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Remove required actions to avoid "Account is not fully set up" message
        user.setRequiredActions(Collections.emptyList());

        return user;
    }

    private void setDefaultPassword(String keycloakUserId) {
        log.debug("Setting default password for user: {}", keycloakUserId);

        try {
            UserResource userResource = getUserResource(keycloakUserId);

            // Set password as non-temporary
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(keycloakProperties.getUser().getDefaultPassword());
            credential.setTemporary(false);

            userResource.resetPassword(credential);

            // Ensure no required actions are set
            UserRepresentation user = userResource.toRepresentation();
            user.setRequiredActions(Collections.emptyList());
            userResource.update(user);

            log.info("Default password set for user: {}", keycloakUserId);

        } catch (Exception e) {
            log.error("Error setting default password for user: {}", keycloakUserId, e);
        }
    }

    private RealmResource getRealmResource() {
        return keycloakAdminClient.realm(keycloakProperties.getAdmin().getRealm());
    }

    private UserResource getUserResource(String keycloakUserId) {
        return getRealmResource().users().get(keycloakUserId);
    }
}

