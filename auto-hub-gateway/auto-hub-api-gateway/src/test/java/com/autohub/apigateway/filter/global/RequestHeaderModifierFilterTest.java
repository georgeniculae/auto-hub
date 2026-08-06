package com.autohub.apigateway.filter.global;

import com.autohub.apigateway.security.JwtAuthenticationTokenConverter;
import com.autohub.apigateway.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHeaderModifierFilterTest {

    @InjectMocks
    private RequestHeaderModifierFilter requestHeaderModifierFilter;

    @Mock
    private NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;

    @Mock
    private JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter;

    @Mock
    private GatewayFilterChain chain;

    @Test
    void filterTest_success() {
        String tokenValue = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt =
                new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(jwtAuthenticationTokenConverter.extractEmail(any(Jwt.class))).thenReturn("user@mail.com");
        when(jwtAuthenticationTokenConverter.extractGrantedAuthorities(any(Jwt.class)))
                .thenReturn(Flux.just(simpleGrantedAuthority));
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_noAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/rental-offices/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_notCorrespondingPath() {
        String tokenValue = TestUtil.getResourceAsJson("/data/JwtToken.json", String.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/swagger-ui.html")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        Map<String, Object> headers = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        Map<String, Object> claims = Map.of("preferred_username", "user");

        Jwt jwt =
                new Jwt(tokenValue, Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES), headers, claims);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        when(nimbusReactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        when(jwtAuthenticationTokenConverter.extractUsername(any(Jwt.class))).thenReturn("user");
        when(jwtAuthenticationTokenConverter.extractEmail(any(Jwt.class))).thenReturn("user@mail.com");
        when(jwtAuthenticationTokenConverter.extractGrantedAuthorities(any(Jwt.class)))
                .thenReturn(Flux.just(simpleGrantedAuthority));
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void filterTest_availableCarsPath_noAuthorizationRequired() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/cars/available")
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(chain).filter(exchange);
        verifyNoInteractions(nimbusReactiveJwtDecoder);
    }

    @Test
    void filterTest_mcpPath_noAuthorizationRequired() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/mcp")
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(chain).filter(exchange);
        verifyNoInteractions(nimbusReactiveJwtDecoder);
    }

    @Test
    void filterTest_carAvailabilityPath_stillRequiresAuthorization() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/agency/cars/{id}/availability", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        requestHeaderModifierFilter.filter(exchange, chain)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
        verifyNoInteractions(chain);
    }

}
