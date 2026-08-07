package com.autohub.ai.config;

import com.autohub.lib.config.restclient.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AiRestClientConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(RestClientConfig.class, AiRestClientConfig.class);

    /**
     * Mirrors how {@code OllamaApiAutoConfiguration} obtains its builder: it asks the context for
     * whatever {@code RestClient.Builder} is available. Handing it the load-balanced one makes every
     * Ollama call fail with "Service Instance cannot be null, serviceId: localhost".
     */
    @Test
    void springAiDoesNotResolveTheLoadBalancedBuilder() {
        contextRunner.run(context -> {
            ObjectProvider<RestClient.Builder> provider = context.getBeanProvider(RestClient.Builder.class);
            RestClient.Builder resolved = provider.getIfAvailable(RestClient::builder);

            assertThat(resolved).isSameAs(context.getBean("plainRestClientBuilder"));
            assertThat(resolved).isNotSameAs(context.getBean("loadBalancedRestClientBuilder"));
        });
    }

    @Test
    void bothBuildersCoexistWithoutAmbiguity() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("plainRestClientBuilder");
            assertThat(context).hasBean("loadBalancedRestClientBuilder");
            assertThat(context).hasSingleBean(RestClient.class);
        });
    }

}