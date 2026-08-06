package com.autohub.autohubmcp;

import com.autohub.autohubmcp.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Starts a real Spring context on a random port and hits the actual MCP transport over HTTP with no
 * credentials, the way the regression this guards against was found: the module's other five tests are
 * all unit tests, so a completely broken (401-everything) service still had a green suite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.loadbalancer.enabled=false"
})
class McpServerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockitoBean
    private CarService carService;

    @Test
    void toolsListTest_returnsAvailableCarsToolWithNoCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM));

        String requestBody = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"tools/list\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = testRestTemplate.postForEntity("/mcp", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("get_available_cars"));
    }

}
