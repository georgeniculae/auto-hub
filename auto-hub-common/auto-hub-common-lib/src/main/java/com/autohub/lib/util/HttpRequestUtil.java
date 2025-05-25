package com.autohub.lib.util;

import com.autohub.exception.AutoHubException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
public class HttpRequestUtil {

    public static Consumer<HttpHeaders> setHttpHeaders(String apiKey, List<String> roles) {
        return httpHeaders -> {
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add(Constants.X_API_KEY, apiKey);
            httpHeaders.addAll(Constants.X_ROLES, roles);
        };
    }

    public static String extractUsername() {
        HttpServletRequest request = getRequest();

        return Optional.ofNullable(request.getHeader(Constants.X_USERNAME))
                .orElse(StringUtils.EMPTY);
    }

    public static String extractEmail() {
        HttpServletRequest request = getRequest();

        return Optional.ofNullable(request.getHeader(Constants.X_EMAIL))
                .orElse(StringUtils.EMPTY);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return Optional.ofNullable(requestAttributes)
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(() -> new AutoHubException("Request attributes are null"));
    }

}
