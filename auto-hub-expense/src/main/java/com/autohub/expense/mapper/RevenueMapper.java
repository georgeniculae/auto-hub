package com.autohub.expense.mapper;

import com.autohub.dto.expense.RevenueResponse;
import com.autohub.entity.invoice.Revenue;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RevenueMapper {

    RevenueResponse mapEntityToDto(Revenue revenue);

}
