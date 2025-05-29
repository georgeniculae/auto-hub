package com.autohub.audit.util;

import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@UtilityClass
public class TestUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T getResourceAsJson(String resourceName, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(getRespurceAsString(resourceName), valueType);
        } catch (JsonProcessingException e) {
            throw new AutoHubNotFoundException("Failed getting resource: " + resourceName + ", cause: " + e.getMessage());
        }
    }

    private static String getRespurceAsString(String resourceName) {
        URL resource = TestUtil.class.getResource(resourceName);

        if (resource == null) {
            throw new AutoHubException("Failed getting resource: " + resourceName);
        }

        try {
            return new String(Files.readAllBytes(Paths.get(resource.toURI())));
        } catch (IOException | URISyntaxException e) {
            throw new AutoHubException("Failed getting resource: " + resourceName);
        }
    }

}
