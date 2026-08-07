package com.autohub.ai.util;

import com.autohub.dto.common.AuthenticationInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyProvider {

    @Value("${apikey.secret}")
    private String apiKey;

    @Value("${apikey.system-roles}")
    private List<String> systemRoles;

    public AuthenticationInfo getAuthenticationInfo() {
        return AuthenticationInfo.builder()
                .apikey(apiKey)
                .roles(systemRoles)
                .build();
    }

}
