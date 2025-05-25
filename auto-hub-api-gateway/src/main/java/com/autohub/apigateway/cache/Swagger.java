package com.autohub.apigateway.cache;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import lombok.Builder;

@Builder
public record Swagger(String identifier, OpenApiInteractionValidator swaggerValidator) {
}
