package com.autohub.autohubmcp.service;

import com.autohub.autohubmcp.util.TestUtil;
import com.autohub.dto.agency.CarResponse;
import com.autohub.exception.AutoHubResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CarServiceTest {

    private static final String URL = "http://auto-hub-agency/agency/cars";
    private static final String AVAILABLE_URL = URL + "/available";

    private CarService carService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        carService = new CarService(restClientBuilder.build());
        ReflectionTestUtils.setField(carService, "url", URL);
    }

    @Test
    void getAllAvailableCarsTest_success() {
        String body = TestUtil.getResourceAsString("/data/CarResponses.json");

        mockServer.expect(requestTo(AVAILABLE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        List<CarResponse> actualCars = carService.getAllAvailableCars();

        assertEquals(1, actualCars.size());
        assertEquals("Volkswagen", actualCars.getFirst().make());
        mockServer.verify();
    }

    @Test
    void getAllAvailableCarsTest_noCarsAvailable() {
        mockServer.expect(requestTo(AVAILABLE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertTrue(carService.getAllAvailableCars().isEmpty());
        mockServer.verify();
    }

    @Test
    void getAllAvailableCarsTest_agencyError() {
        mockServer.expect(requestTo(AVAILABLE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(AutoHubResponseStatusException.class, () -> carService.getAllAvailableCars());
        mockServer.verify();
    }

}
