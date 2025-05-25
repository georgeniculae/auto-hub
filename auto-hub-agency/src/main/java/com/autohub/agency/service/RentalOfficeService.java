package com.autohub.agency.service;

import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.mapper.RentalOfficeMapper;
import com.autohub.agency.repository.RentalOfficeRepository;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import com.autohub.exception.AutoHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RentalOfficeService {

    private final RentalOfficeRepository rentalOfficeRepository;
    private final RentalOfficeMapper rentalOfficeMapper;

    @Transactional(readOnly = true)
    public List<RentalOfficeResponse> findAllRentalOffices() {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findAllRentalOffices()) {
            return rentalOfficeStream.map(rentalOfficeMapper::mapEntityToDto).toList();
        }
    }

    public void deleteRentalOfficeById(Long id) {
        rentalOfficeRepository.deleteById(id);
    }

    public RentalOfficeResponse findRentalOfficeById(Long id) {
        RentalOffice rentalOffice = findEntityById(id);

        return rentalOfficeMapper.mapEntityToDto(rentalOffice);
    }

    public RentalOffice findEntityById(Long id) {
        return rentalOfficeRepository.findById(id)
                .orElseThrow(() -> new AutoHubNotFoundException("Rental office with id " + id + " does not exist"));
    }

    public RentalOfficeResponse saveRentalOffice(RentalOfficeRequest rentalOfficeRequest) {
        RentalOffice rentalOffice = rentalOfficeMapper.getNewRentalOffice(rentalOfficeRequest);
        RentalOffice savedRentalOffice = saveEntity(rentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeResponse updateRentalOffice(Long id, RentalOfficeRequest updatedRentalOfficeRequest) {
        RentalOffice existingRentalOffice = findEntityById(id);

        existingRentalOffice.setName(updatedRentalOfficeRequest.name());
        existingRentalOffice.setContactAddress(updatedRentalOfficeRequest.contactAddress());
        existingRentalOffice.setPhoneNumber(updatedRentalOfficeRequest.phoneNumber());

        RentalOffice savedRentalOffice = saveEntity(existingRentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    @Transactional(readOnly = true)
    public List<RentalOfficeResponse> findRentalOfficeByFilter(String filter) {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findRentalOfficeByFilter(filter)) {
            return rentalOfficeStream.map(rentalOfficeMapper::mapEntityToDto).toList();
        }
    }

    public Long countRentalOffices() {
        return rentalOfficeRepository.count();
    }

    private RentalOffice saveEntity(RentalOffice existingRentalOffice) {
        return rentalOfficeRepository.save(existingRentalOffice);
    }

}
