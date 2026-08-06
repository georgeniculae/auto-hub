package com.autohub.agency.controller;

import com.autohub.agency.security.SecurityConfig;
import com.autohub.agency.service.CarService;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.CarResponse;
import com.autohub.lib.security.AuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        CarController.class,
        SecurityConfig.class,
        CarControllerSecurityTest.TestSecurityConfig.class
})
@AutoConfigureMockMvc
@EnableWebMvc
class CarControllerSecurityTest {

    private static final String PATH = "/cars";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
            return mock(AuthenticationProvider.class);
        }

        @Bean
        AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
            return new AuthenticationFilter(authenticationManager);
        }

    }

    @Test
    @WithAnonymousUser
    void findAllAvailableCarsTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAllAvailableCars()).thenReturn(List.of(carResponse));

        String content = mockMvc.perform(get(PATH + "/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(content);
    }

    @Test
    @WithAnonymousUser
    void findAllCarsTest_forbidden() throws Exception {
        // 403, not 401: SecurityConfig disables httpBasic and formLogin without registering an
        // AuthenticationEntryPoint, so Spring Security falls back to Http403ForbiddenEntryPoint.
        mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
