package com.autohub.apigateway.scheduler;

import com.autohub.apigateway.cache.CacheUpdateService;
import com.autohub.apigateway.cache.CaffeineCacheConfig;
import com.autohub.apigateway.cache.OpenApiCache;
import com.autohub.apigateway.config.property.RegisteredEndpoints;
import com.autohub.apigateway.retry.RetryHandler;
import com.autohub.apigateway.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        CacheUpdateScheduler.class,
        CaffeineCacheConfig.class,
        CacheUpdateService.class,
        OpenApiCache.class
})
class CacheUpdateSchedulerTest {

    private static String agencyContent;
    private static String aiContent;
    private static String bookingsContent;
    private static String customersContent;
    private static String expenseContent;
    private static List<RegisteredEndpoints.RegisteredEndpoint> endpoints;

    @Autowired
    private CacheUpdateScheduler cacheUpdateScheduler;

    @Autowired
    private OpenApiCache openApiCache;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockitoBean
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @MockitoBean
    private RegisteredEndpoints registeredEndpoints;

    @MockitoBean
    private RetryHandler retryHandler;

    @BeforeAll
    static void setUp() {
        agencyContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveAgencySwagger.json", String.class);
        aiContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveAiSwagger.json", String.class);
        bookingsContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveBookingsSwagger.json", String.class);
        customersContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveCustomersSwagger.json", String.class);
        expenseContent =
                TestUtil.getResourceAsJson("/data/AutoHubReactiveExpenseSwagger.json", String.class);

        endpoints = List.of(
                new RegisteredEndpoints.RegisteredEndpoint("agency", "agency-url"),
                new RegisteredEndpoints.RegisteredEndpoint("ai", "ai-url"),
                new RegisteredEndpoints.RegisteredEndpoint("bookings", "bookings-url"),
                new RegisteredEndpoints.RegisteredEndpoint("customers", "customers-url"),
                new RegisteredEndpoints.RegisteredEndpoint("expense", "expense-url")
        );
    }

    @AfterEach
    void tearDown() {
        openApiCache.toMap().clear();
    }

    @Test
    @SuppressWarnings("all")
    void updateCacheTest_success() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.just(expenseContent);
                }
            }
        });
        when(retryHandler.retry()).thenReturn(Retry.backoff(0, Duration.ZERO));

        assertDoesNotThrow(() -> cacheUpdateScheduler.updateCache());
        assertEquals(5, openApiCache.toMap().size());
    }

    @Test
    @SuppressWarnings("all")
    void updateCache_errorWhenGettingExpenseSwagger() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.error(new RuntimeException("Test"));
                }
            }
        });
        when(retryHandler.retry()).thenReturn(Retry.backoff(0, Duration.ZERO));

        assertDoesNotThrow(() -> cacheUpdateScheduler.updateCache());
        assertEquals(4, openApiCache.toMap().size());
    }

    @Test
    @SuppressWarnings("all")
    void updateCache_emptyExpenseSwagger() {
        when(registeredEndpoints.getEndpoints()).thenReturn(endpoints);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;

                if (count == 1) {
                    return Mono.just(agencyContent);
                } else if (count == 2) {
                    return Mono.just(aiContent);
                } else if (count == 3) {
                    return Mono.just(bookingsContent);
                } else if (count == 4) {
                    return Mono.just(customersContent);
                } else {
                    return Mono.empty();
                }
            }
        });
        when(retryHandler.retry()).thenReturn(Retry.backoff(0, Duration.ZERO));

        assertDoesNotThrow(() -> cacheUpdateScheduler.updateCache());
        assertEquals(4, openApiCache.toMap().size());
    }

}
