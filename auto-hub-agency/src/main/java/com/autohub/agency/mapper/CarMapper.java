package com.autohub.agency.mapper;

import com.autohub.agency.entity.BodyType;
import com.autohub.agency.entity.Car;
import com.autohub.agency.entity.CarStatus;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.dto.agency.BodyCategory;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.CarState;
import com.autohub.exception.AutoHubException;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CarMapper {

    @Mapping(target = "initialRentalOfficeId", expression = "java(car.getInitialRentalOffice().getId())")
    @Mapping(target = "actualRentalOfficeId", expression = "java(car.getActualRentalOffice().getId())")
    @Mapping(target = "carLocation", expression = "java(car.getActualRentalOffice().getCity())")
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarResponse mapEntityToDto(Car car);

    @Mapping(target = "bodyType", expression = "java(mapToBodyType(carRequest.bodyCategory()))")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(carRequest.carState()))")
    @Mapping(target = "image", expression = "java(mapToImage(image))")
    @Mapping(target = "initialRentalOffice", expression = "java(initialRentalOffice)")
    @Mapping(target = "actualRentalOffice", expression = "java(actualRentalOffice)")
    Car getNewCar(CarRequest carRequest, MultipartFile image, RentalOffice initialRentalOffice, RentalOffice actualRentalOffice);

    @Mapping(target = "actualRentalOfficeId", expression = "java(car.getActualRentalOffice().getId())")
    AvailableCarInfo mapToAvailableCarInfo(Car car);

    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carLocation", expression = "java(car.getActualRentalOffice().getCity())")
    AvailableCarDetails mapEntityToAvailableCarDetails(Car car);

    default BodyType mapToBodyType(BodyCategory bodyCategory) {
        return BodyType.valueOf(bodyCategory.name());
    }

    default CarStatus mapToCarStatus(CarState carState) {
        return CarStatus.valueOf(carState.name());
    }

    default byte[] mapToImage(MultipartFile multipartFile) {
        try {
            if (ObjectUtils.isEmpty(multipartFile)) {
                return null;
            }

            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new AutoHubException(e.getMessage());
        }
    }

}
