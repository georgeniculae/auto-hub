package com.autohub.lib.aspect;

import com.autohub.dto.common.AuditLogInfoRequest;
import com.autohub.lib.exceptionhandling.ExceptionUtil;
import com.autohub.lib.service.AuditLogProducerService;
import com.autohub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Around("@annotation(com.autohub.lib.aspect.LogActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogActivity logActivity = method.getAnnotation(LogActivity.class);

        log.info("Method called: {}", signature);

        String username = HttpRequestUtil.extractUsername();
        List<String> parametersValues = getParametersValues(joinPoint, logActivity, signature);

        AuditLogInfoRequest auditLogInfoRequest = getAuditLogInfoRequest(method.getName(), username, parametersValues);

        try {
            Object proceed = joinPoint.proceed();
            auditLogProducerService.sendAuditLog(auditLogInfoRequest);

            return proceed;
        } catch (Throwable e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private List<String> getParametersValues(
            ProceedingJoinPoint joinPoint,
            LogActivity logActivity,
            MethodSignature signature
    ) {
        return Optional.ofNullable(logActivity)
                .stream()
                .flatMap(activity -> Stream.of(activity.sentParameters()))
                .map(parameter -> {
                    List<String> parameters = Arrays.asList(signature.getParameterNames());
                    int indexOfElement = parameters.indexOf(parameter);

                    if (indexOfElement < 0) {
                        return StringUtils.EMPTY;
                    }

                    return joinPoint.getArgs()[indexOfElement].toString();
                })
                .toList();
    }

    private AuditLogInfoRequest getAuditLogInfoRequest(String methodName, String username, List<String> parametersValues) {
        return new AuditLogInfoRequest(methodName, username, LocalDateTime.now(), parametersValues);
    }

}
