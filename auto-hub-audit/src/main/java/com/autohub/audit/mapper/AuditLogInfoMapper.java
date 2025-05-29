package com.autohub.audit.mapper;

import com.autohub.audit.entity.AuditLogInfo;
import com.autohub.dto.common.AuditLogInfoRequest;
import com.autohub.dto.common.ParameterInfo;
import com.autohub.exception.AutoHubException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface AuditLogInfoMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mapping(target = "parametersInfo", expression = "java(convertToString(auditLogInfoRequest.parameters()))")
    AuditLogInfo mapDtoToBookingEntity(AuditLogInfoRequest auditLogInfoRequest);

    default String convertToString(List<ParameterInfo> parameterInfos) {
        try {
            return OBJECT_MAPPER.writeValueAsString(parameterInfos);
        } catch (JsonProcessingException e) {
            throw new AutoHubException(e.getMessage());
        }
    }

}
