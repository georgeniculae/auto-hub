package com.autohub.customer.service;

import com.autohub.customer.mapper.CustomerMapper;
import com.autohub.customer.mapper.CustomerMapperImpl;
import com.autohub.customer.util.AssertionUtil;
import com.autohub.customer.util.TestData;
import com.autohub.customer.util.TestUtil;
import com.autohub.dto.customer.RegisterRequest;
import com.autohub.dto.customer.RegistrationResponse;
import com.autohub.dto.customer.UserInfo;
import com.autohub.dto.customer.UserUpdateRequest;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.security.ApiKeyAuthenticationToken;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private Keycloak keycloak;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RealmResource realmResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleRepresentation roleRepresentation;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @Mock
    private UsernameProducerService usernameProducerService;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapperImpl();

    @Test
    void findAllUsersTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list()).thenReturn(List.of(userRepresentation));

        List<UserInfo> allCustomers = customerService.findAllUsers();

        assertFalse(allCustomers.isEmpty());
    }

    @Test
    void getCurrentUserTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-USERNAME", "user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));

        UserInfo currentUser = customerService.getCurrentUser();

        AssertionUtil.assertUserDetails(userRepresentation, currentUser);

        verify(customerMapper).mapUserToUserInfo(any(UserRepresentation.class));
    }

    @Test
    void getCurrentUserTest_errorOnFindingByUsername() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> customerService.findUserByUsername("user"));
    }

    @Test
    void getCurrentUserTest_noUsersFound() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of());

        AutoHubNotFoundException notFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> customerService.getCurrentUser());

        assertNotNull(notFoundException);
    }

    @Test
    void findUserByUsernameTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));

        UserInfo user = customerService.findUserByUsername("user");

        AssertionUtil.assertUserDetails(userRepresentation, user);
    }

    @Test
    void findUserByUsernameTest_noUserFound() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of());

        AutoHubNotFoundException notFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> customerService.findUserByUsername("user"));

        assertNotNull(notFoundException);
    }

    @Test
    void registerCustomerTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        Headers<Object> headers = new Headers<>();
        headers.put("test", List.of());
        Response response = new ServerResponse(null, 201, headers);

        try (var _ = mockStatic(CreatedResponseUtil.class)) {
            when(CreatedResponseUtil.getCreatedId(any())).thenReturn("id");
            when(keycloak.realm(anyString())).thenReturn(realmResource);
            when(realmResource.roles()).thenReturn(rolesResource);
            when(rolesResource.list()).thenReturn(List.of(roleRepresentation));
            when(rolesResource.get(anyString())).thenReturn(roleResource);
            when(realmResource.users()).thenReturn(usersResource);
            when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
            when(usersResource.get(anyString())).thenReturn(userResource);
            when(rolesResource.get(anyString())).thenReturn(roleResource);
            doNothing().when(roleResource).addComposites(anyList());
            when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
            doNothing().when(userResource).resetPassword(any(CredentialRepresentation.class));
            when(userResource.roles()).thenReturn(roleMappingResource);
            when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
            doNothing().when(roleScopeResource).add(anyList());

            RegistrationResponse registrationResponse = customerService.registerCustomer(registerRequest);

            AssertionUtil.assertRegistrationResponse(registerRequest, registrationResponse);

            verify(customerMapper).mapToRegistrationResponse(any(UserRepresentation.class));
        }
    }

    @Test
    void registerCustomerTest_customerUnderAge() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestAgeBelow18.json", RegisterRequest.class);

        AutoHubResponseStatusException autoHubResponseStatusException =
                assertThrows(AutoHubResponseStatusException.class, () -> customerService.registerCustomer(registerRequest));

        assertNotNull(autoHubResponseStatusException);
        assertThat(autoHubResponseStatusException.getMessage()).contains("Customer is under 18 years old");
    }

    @Test
    void registerCustomerTest_passwordTooShort() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        AutoHubResponseStatusException autoHubResponseStatusException =
                assertThrows(AutoHubResponseStatusException.class, () -> customerService.registerCustomer(registerRequest));

        assertNotNull(autoHubResponseStatusException);
        assertThat(autoHubResponseStatusException.getMessage()).contains("Password too short");
    }

    @Test
    void updateUserTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).update(any(UserRepresentation.class));

        UserInfo userInfo = customerService.updateUser("user", userUpdateRequest);

        AssertionUtil.assertUserDetails(userUpdateRequest, userInfo);
    }

    @Test
    void updateUserTest_errorOnFindingById() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doThrow(new NotFoundException()).when(userResource).update(any(UserRepresentation.class));

        AutoHubNotFoundException notFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> customerService.updateUser("user", userUpdateRequest));

        assertNotNull(notFoundException);
    }

    @Test
    void deleteUserByUsernameTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).remove();
        doNothing().when(usernameProducerService).sendUsername(anyString());
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));

        assertDoesNotThrow(() -> customerService.deleteUserByUsername("user"));
    }

    @Test
    void deleteUserByUsernameTest_userNotFound() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        when(usersResource.get(anyString())).thenReturn(userResource);

        doThrow(new NotFoundException()).when(userResource).remove();

        assertThrows(AutoHubNotFoundException.class, () -> customerService.deleteUserByUsername("user"));
    }

    @Test
    void logoutTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).logout();

        assertDoesNotThrow(() -> customerService.signOut());
    }

    @Test
    void logoutTest_errorOnLogout() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        UserRepresentation userRepresentation = TestData.getUserRepresentation();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByUsername(anyString(), anyBoolean())).thenReturn(List.of(userRepresentation));
        when(usersResource.get(anyString())).thenReturn(userResource);
        doThrow(new NotFoundException()).when(userResource).logout();

        AutoHubNotFoundException notFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> customerService.signOut());

        assertNotNull(notFoundException);
    }

}
