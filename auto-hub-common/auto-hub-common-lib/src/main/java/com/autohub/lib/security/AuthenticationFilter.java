package com.autohub.lib.security;

import com.autohub.lib.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String apikey = request.getHeader(Constants.X_API_KEY);

        if (ObjectUtils.isNotEmpty(apikey)) {
            List<SimpleGrantedAuthority> roles = getRoles(request);

            ApiKeyAuthenticationToken apiKeyAuthenticationToken = new ApiKeyAuthenticationToken(roles, apikey);
            Authentication authenticate = authenticationManager.authenticate(apiKeyAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> getRoles(HttpServletRequest request) {
        return Collections.list(request.getHeaders(Constants.X_ROLES))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
