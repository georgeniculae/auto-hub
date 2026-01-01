package com.autohub.customer.controller;

import com.autohub.customer.service.CustomerService;
import com.autohub.customer.util.TestUtil;
import com.autohub.dto.customer.RegisterRequest;
import com.autohub.dto.customer.RegistrationResponse;
import com.autohub.dto.customer.UserInfo;
import com.autohub.dto.customer.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CustomerController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class CustomerControllerTest {

    private static final String PATH = "/customers";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllUsersTest_success() throws Exception {
        UserInfo userInfo = TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(customerService.findAllUsers()).thenReturn(List.of(userInfo));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/infos")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllUsersTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/infos")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentUserTest_success() throws Exception {
        mockMvc.perform(get(PATH + "/current")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getCurrentUserTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/current")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void registerUserTest_success() throws Exception {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        String content = TestUtil.writeValueAsString(registerRequest);

        when(customerService.registerCustomer(any(RegisterRequest.class))).thenReturn(registrationResponse);

        MockHttpServletResponse response = mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void registerUserTest_forbidden() throws Exception {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        String content = TestUtil.writeValueAsString(registerRequest);

        mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void registerUserTest_unauthorized() throws Exception {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        String content = TestUtil.writeValueAsString(registerRequest);

        mockMvc.perform(post(PATH + "/register")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateUserTest_success() throws Exception {
        UserInfo userInfo = TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtil.writeValueAsString(userInfo);

        when(customerService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(userInfo);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_forbidden() throws Exception {
        UserInfo userInfo = TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtil.writeValueAsString(userInfo);

        mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_unauthorized() throws Exception {
        UserInfo userInfo = TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        String content = TestUtil.writeValueAsString(userInfo);

        mockMvc.perform(put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findUserByUsernameTest_success() throws Exception {
        UserInfo userInfo = TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(customerService.findUserByUsername(anyString())).thenReturn(userInfo);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{username}", "admin")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findUserByUsernameTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "admin")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countUsersTest_success() throws Exception {
        when(customerService.countUsers()).thenReturn(1);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countUsersTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/count")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteUserByUsernameTest_success() throws Exception {
        doNothing().when(customerService).deleteUserByUsername(anyString());

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{username}", "user")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteUserByUsernameTest_unauthorized() throws Exception {
        mockMvc.perform(delete(PATH + "/{username}", "user")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteCurrentTest_success() throws Exception {
        doNothing().when(customerService).deleteCurrentUser();

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/current")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteCurrentTest_unauthorized() throws Exception {
        mockMvc.perform(delete(PATH + "/current")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void logoutTest_success() throws Exception {
        doNothing().when(customerService).signOut();

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/sign-out")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void logoutTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/sign-out")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
