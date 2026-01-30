package com.autohub.lib.util;

import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.lib.security.ApiKeyAuthenticationToken;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class AuthenticationUtil {

    public static AuthenticationInfo getAuthenticationInfo() {
        ApiKeyAuthenticationToken principal = getAuthentication();
        String apikey = principal.getName();
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        String username = HttpRequestUtil.extractUsername();
        String email = HttpRequestUtil.extractEmail();

        return AuthenticationInfo.builder()
                .apikey(apikey)
                .username(username)
                .email(email)
                .roles(extractRoles(authorities))
                .build();
    }

    private static ApiKeyAuthenticationToken getAuthentication() {
        return (ApiKeyAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    private static List<String> extractRoles(Collection<GrantedAuthority> authorities) {
        if (ObjectUtils.isEmpty(authorities)) {
            return List.of();
        }

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

}
