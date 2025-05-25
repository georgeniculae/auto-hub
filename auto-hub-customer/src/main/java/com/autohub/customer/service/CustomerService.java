package com.autohub.customer.service;

import com.autohub.customer.mapper.CustomerMapper;
import com.autohub.customer.util.Constants;
import com.autohub.dto.customer.RegisterRequest;
import com.autohub.dto.customer.RegistrationResponse;
import com.autohub.dto.customer.UserInfo;
import com.autohub.dto.customer.UserUpdateRequest;
import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.util.HttpRequestUtil;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements RetryListener {

    private final Keycloak keycloak;
    private final UsernameProducerService usernameProducerService;
    private final CustomerMapper customerMapper;

    @Value("${keycloak.realm}")
    private String realm;

    public List<UserInfo> findAllUsers() {
        return getUsersResource().list()
                .stream()
                .map(customerMapper::mapUserToUserInfo)
                .toList();
    }

    public UserInfo findUserByUsername(String username) {
        UserRepresentation userRepresentation = getUserRepresentation(username);

        return customerMapper.mapUserToUserInfo(userRepresentation);
    }

    public UserInfo getCurrentUser() {
        String username = HttpRequestUtil.extractUsername();

        return findUserByUsername(username);
    }

    public Integer countUsers() {
        return getUsersResource().count();
    }

    public RegistrationResponse registerCustomer(RegisterRequest request) {
        validateRequest(request);
        UserRepresentation userRepresentation = createUserRepresentation(request);

        try (Response response = getUsersResource().create(userRepresentation)) {
            final int statusCode = response.getStatus();

            if (HttpStatus.CREATED.value() == statusCode) {
                return getRegistrationResponse(userRepresentation, response, request);
            }

            throw new AutoHubResponseStatusException(
                    HttpStatusCode.valueOf(statusCode),
                    "User could not be created: " + response.getStatusInfo().getReasonPhrase()
            );
        }
    }

    public UserInfo updateUser(String id, UserUpdateRequest userUpdateRequest) {
        UserResource userResource = findById(id);

        UserRepresentation userRepresentation = customerMapper.mapToUserRepresentation(userUpdateRequest);
        userRepresentation.singleAttribute(Constants.ADDRESS, userUpdateRequest.address());
        userRepresentation.singleAttribute(Constants.DATE_OF_BIRTH, userUpdateRequest.dateOfBirth().toString());

        try {
            userResource.update(userRepresentation);
        } catch (Exception e) {
            handleRestEasyCallException(e);
        }

        return customerMapper.mapUserToUserInfo(userRepresentation);
    }

    public void deleteUserByUsername(String username) {
        UserRepresentation userRepresentation = getUserRepresentation(username);
        UserResource userResource = findById(userRepresentation.getId());

        try {
            userResource.remove();
        } catch (Exception e) {
            handleRestEasyCallException(e);
        }

        usernameProducerService.sendUsername(username);
    }

    public void deleteCurrentUser() {
        String username = HttpRequestUtil.extractUsername();
        deleteUserByUsername(username);
    }

    public void signOut() {
        String username = HttpRequestUtil.extractUsername();
        UserRepresentation userRepresentation = getUserRepresentation(username);

        try {
            findById(userRepresentation.getId()).logout();
        } catch (Exception e) {
            handleRestEasyCallException(e);
        }
    }

    private UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    private UserResource findById(String id) {
        return getUsersResource().get(id);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);

        return passwordCredentials;
    }

    private UserRepresentation createUserRepresentation(RegisterRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setCredentials(List.of(createPasswordCredentials(request.password())));
        userRepresentation.singleAttribute(Constants.ADDRESS, request.address());
        userRepresentation.singleAttribute(Constants.DATE_OF_BIRTH, request.dateOfBirth().toString());
        userRepresentation.setEmailVerified(!request.needsEmailVerification());
        userRepresentation.setEnabled(true);

        return userRepresentation;
    }

    private void verifyEmail(String userId) {
        try {
            findById(userId).sendVerifyEmail();
        } catch (Exception e) {
            handleRestEasyCallException(e);
        }
    }

    private RegistrationResponse getRegistrationResponse(UserRepresentation userRepresentation, Response response,
                                                         RegisterRequest request) {
        String createdId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = findById(createdId);
        createRoleUserIfNonexistent();

        try {
            userResource.resetPassword(createPasswordCredentials(request.password()));
            RoleRepresentation roleRepresentation = getUserRoleRepresentation();
            userResource.roles().realmLevel().add(List.of(roleRepresentation));
        } catch (Exception e) {
            handleRestEasyCallException(e);
        }

        if (request.needsEmailVerification()) {
            verifyEmail(getUserId(userRepresentation.getUsername()));
        }

        return customerMapper.mapToRegistrationResponse(userRepresentation);
    }

    private UserRepresentation getUserRepresentation(String username) {
        List<UserRepresentation> userRepresentations = getUsersResource().searchByUsername(username, true);

        if (userRepresentations.isEmpty()) {
            throw new AutoHubNotFoundException("User with username " + username + " doesn't exist");
        }

        return userRepresentations.getFirst();
    }

    private String getUserId(String username) {
        return getUserRepresentation(username).getId();
    }

    private void createRoleUserIfNonexistent() {
        boolean isRoleNonexistent = getRealmResource().roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .noneMatch(Constants.USER::equals);

        if (isRoleNonexistent) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(Constants.USER);
            roleRepresentation.setDescription(Constants.$ + Constants.OPENING_BRACE + Constants.ROLE + Constants.USER + Constants.CLOSE_BRACE);

            RolesResource rolesResource = getRolesResource();
            rolesResource.create(roleRepresentation);

            getRoleResource().addComposites(List.of(rolesResource.get(Constants.OFFLINE_ACCESS).toRepresentation()));
        }
    }

    private RoleRepresentation getUserRoleRepresentation() {
        return getRoleResource().toRepresentation();
    }

    private RoleResource getRoleResource() {
        return getRolesResource().get(Constants.USER);
    }

    private RolesResource getRolesResource() {
        return getRealmResource().roles();
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }

    private void validateRequest(RegisterRequest request) {
        if (Optional.ofNullable(request.password()).orElseThrow().length() < 8) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }

        if (Period.between(Optional.ofNullable(request.dateOfBirth()).orElseThrow(), LocalDate.now()).getYears() < 18) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is under 18 years old");
        }
    }

    private void handleRestEasyCallException(Exception e) {
        if (e instanceof NotFoundException) {
            throw new AutoHubNotFoundException("User not found");
        }

        if (e instanceof ErrorResponse errorResponse) {
            throw new AutoHubResponseStatusException(
                    HttpStatusCode.valueOf(errorResponse.getStatusCode().value()),
                    e.getMessage()
            );
        }

        throw new AutoHubException(e.getMessage());
    }

}
