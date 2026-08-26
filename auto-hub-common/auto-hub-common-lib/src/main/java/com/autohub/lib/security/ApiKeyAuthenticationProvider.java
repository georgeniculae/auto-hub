package com.autohub.lib.security;

import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value("${apikey.secret}")
    private String apiKeySecret;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = Objects.requireNonNull(authentication.getPrincipal()).toString();

        if (apiKeySecret.equals(apiKey)) {
            ApiKeyAuthenticationToken apiKeyAuthenticationToken = (ApiKeyAuthenticationToken) authentication;

            return new ApiKeyAuthenticationToken(apiKeyAuthenticationToken);
        }

        throw new AutoHubException("API Key is invalid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
