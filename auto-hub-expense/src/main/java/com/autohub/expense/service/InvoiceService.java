package com.autohub.expense.service;

import com.autohub.dto.common.BookingResponse;
import com.autohub.dto.common.InvoiceResponse;
import com.autohub.dto.expense.InvoiceRequest;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.expense.entity.Invoice;
import com.autohub.expense.mapper.InvoiceMapper;
import com.autohub.expense.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements RetryListener {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllInvoices() {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findAllInvoices()) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllActiveInvoices() {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findAllActive()) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAllInvoicesByCustomerUsername(String customerUsername) {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findByCustomerUsername(customerUsername)) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    public InvoiceResponse findInvoiceById(Long id) {
        Invoice invoice = findEntityById(id);

        return invoiceMapper.mapEntityToDto(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findInvoiceByComments(String searchString) {
        try (Stream<Invoice> invoiceStream = invoiceRepository.findByCommentsIgnoreCase(searchString)) {
            return invoiceStream.map(invoiceMapper::mapEntityToDto).toList();
        }
    }

    public Long countInvoices() {
        return invoiceRepository.count();
    }

    public long countAllActiveInvoices() {
        return invoiceRepository.countAllActive();
    }

    public void saveInvoice(BookingResponse newBookingResponse) {
        if (!invoiceRepository.existsByBookingId(newBookingResponse.id())) {
            Invoice invoice = new Invoice();

            invoice.setCustomerUsername(newBookingResponse.customerUsername());
            invoice.setCustomerEmail(newBookingResponse.customerEmail());
            invoice.setCarId(newBookingResponse.carId());
            invoice.setBookingId(newBookingResponse.id());
            invoice.setRentalCarPrice(newBookingResponse.rentalCarPrice());
            invoice.setDateFrom(newBookingResponse.dateFrom());
            invoice.setDateTo(newBookingResponse.dateTo());

            invoiceRepository.save(invoice);

            return;
        }

        throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice already exists");
    }

    public void updateInvoiceAfterBookingUpdate(BookingResponse bookingResponse) {
        try {
            Invoice invoice = findInvoiceByBookingId(bookingResponse.id());
            invoice.setCarId(bookingResponse.carId());

            invoiceRepository.save(invoice);
        } catch (Exception e) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating invoice");
        }
    }

    public InvoiceResponse closeInvoice(Long id, InvoiceRequest invoiceRequest) {
        validateInvoice(invoiceRequest);
        Invoice existingInvoiceUpdated = updateInvoiceWithBookingDetails(id, invoiceRequest);

        return invoiceMapper.mapEntityToDto(existingInvoiceUpdated);
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        invoiceRepository.deleteByBookingId(bookingId);
    }

    public Invoice findEntityById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new AutoHubNotFoundException("Invoice with id " + id + " does not exist"));
    }

    private void validateInvoice(InvoiceRequest invoiceRequest) {
        validateDateOfReturnOfTheCar(invoiceRequest.carReturnDate());

        if (Boolean.TRUE.equals(invoiceRequest.isVehicleDamaged()) && ObjectUtils.isEmpty(invoiceRequest.damageCost())) {
            throw new AutoHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "If the vehicle is damaged, the damage cost cannot be null/empty"
            );
        }
    }

    private Invoice findInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(
                        () -> new AutoHubNotFoundException("Invoice with booking id: " + bookingId + " does not exist")
                );
    }

    private void validateDateOfReturnOfTheCar(LocalDate dateOfReturnOfTheCar) {
        LocalDate currentDate = LocalDate.now();

        if (dateOfReturnOfTheCar.isBefore(currentDate)) {
            throw new AutoHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date of return of the car cannot be in the past"
            );
        }
    }

    private BigDecimal getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? BigDecimal.ZERO : invoiceRequest.damageCost();
    }

    private BigDecimal getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ? BigDecimal.ZERO : invoiceRequest.additionalPayment();
    }

    private Invoice updateInvoiceWithBookingDetails(Long id, InvoiceRequest invoiceRequest) {
        Invoice existingInvoice = findEntityById(id);

        Long receptionistEmployeeId = invoiceRequest.receptionistEmployeeId();

        existingInvoice.setCarReturnDate(invoiceRequest.carReturnDate());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceRequest));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        existingInvoice.setComments(invoiceRequest.comments());
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, invoiceRequest));
        existingInvoice.setReturnBranchId(invoiceRequest.returnBranchId());

        return invoiceRepository.save(existingInvoice);
    }

    private BigDecimal getTotalAmount(Invoice existingInvoice, InvoiceRequest invoiceRequest) {
        LocalDate carReturnDate = invoiceRequest.carReturnDate();
        LocalDate bookingDateTo = existingInvoice.getDateTo();
        LocalDate bookingDateFrom = existingInvoice.getDateFrom();
        BigDecimal carAmount = existingInvoice.getRentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getAmountForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(getDamageCost(invoiceRequest));
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private BigDecimal getAmountForLateReturn(
            LocalDate carReturnDate,
            LocalDate bookingDateTo,
            LocalDate bookingDateFrom,
            BigDecimal carAmount
    ) {
        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(BigDecimal.valueOf(getDaysPeriod(bookingDateTo, carReturnDate)).multiply(BigDecimal.valueOf(2)).multiply(carAmount));
    }

}
