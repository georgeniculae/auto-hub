package com.autohub.lib.aspect;

import com.autohub.dto.common.AuditLogInfoRequest;
import com.autohub.dto.common.ParameterInfo;
import com.autohub.lib.exceptionhandling.ExceptionUtil;
import com.autohub.lib.service.AuditLogProducerService;
import com.autohub.lib.util.HttpRequestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "audit", name = "enabled")
@Slf4j
public class AuditAspect {

    private final AuditLogProducerService auditLogProducerService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.autohub.lib.aspect.LogActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint) {
        AuditLogInfoRequest auditLogInfoRequest = getAuditLogInfoRequest(joinPoint);

        try {
            Object proceed = joinPoint.proceed();
            auditLogProducerService.sendAuditLog(auditLogInfoRequest);

            return proceed;
        } catch (Throwable e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private AuditLogInfoRequest getAuditLogInfoRequest(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.info("Method called: {}", signature);

        String username = HttpRequestUtil.extractUsername();
        LogActivity logActivity = method.getAnnotation(LogActivity.class);
        List<ParameterInfo> parameters = getParameters(joinPoint, logActivity, signature);
        String activityDescription = logActivity.activityDescription();
        String methodName = method.getName();

        return AuditLogInfoRequest.builder()
                .methodName(methodName)
                .activityDescription(activityDescription)
                .username(username)
                .timestamp(LocalDateTime.now())
                .parameters(parameters)
                .build();
    }

    private List<ParameterInfo> getParameters(
            ProceedingJoinPoint joinPoint,
            LogActivity logActivity,
            MethodSignature signature
    ) {
        return Optional.ofNullable(logActivity)
                .stream()
                .flatMap(activity -> Stream.of(activity.sentParameters()))
                .map(parameter -> extractParameterInfo(joinPoint, signature, parameter))
                .filter(ObjectUtils::isNotEmpty)
                .toList();
    }

    private ParameterInfo extractParameterInfo(ProceedingJoinPoint joinPoint, MethodSignature signature, String parameter) {
        List<String> parameters = Arrays.asList(signature.getParameterNames());
        int indexOfElement = parameters.indexOf(parameter);

        if (indexOfElement < 0) {
            return null;
        }

        Object value = joinPoint.getArgs()[indexOfElement];
        String json = getJson(value);

        return createParameterInfo(parameter, json);
    }

    private String getJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ParameterInfo createParameterInfo(String parameter, String value) {
        return ParameterInfo.builder()
                .parameterName(parameter)
                .parameterValue(value)
                .build();
    }

}
