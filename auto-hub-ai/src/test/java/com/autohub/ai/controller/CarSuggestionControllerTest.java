package com.autohub.ai.controller;

import com.autohub.ai.service.CarSuggestionService;
import com.autohub.ai.util.TestUtil;
import com.autohub.dto.ai.CarSuggestionResponse;
import com.autohub.dto.ai.TripInfo;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CarSuggestionController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class CarSuggestionControllerTest {

    private static final String PATH = "/ai";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarSuggestionService carSuggestionService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_success() throws Exception {
        CarSuggestionResponse carSuggestionResponse =
                TestUtil.getResourceAsJson("/data/CarSuggestionResponse.json", CarSuggestionResponse.class);

        when(carSuggestionService.getChatOutput(any(TripInfo.class))).thenReturn(carSuggestionResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city&tripDate=2024-06-20")
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
    void getChatPromptTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city&tripDate=2024-06-20")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getChatPromptTest_missingRequestParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/car-suggestion?destination=Sinaia&peopleCount=3&tripKind=city")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
