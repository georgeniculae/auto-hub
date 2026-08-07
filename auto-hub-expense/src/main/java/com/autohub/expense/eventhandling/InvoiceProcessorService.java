package com.autohub.expense.eventhandling;

import com.autohub.dto.common.BookingClosingDetails;
import com.autohub.dto.common.CarState;
import com.autohub.dto.common.CarUpdateDetails;
import com.autohub.dto.expense.InvoiceReprocessRequest;
import com.autohub.expense.entity.Invoice;
import com.autohub.expense.mapper.InvoiceMapper;
import com.autohub.expense.producer.BookingUpdateProducerService;
import com.autohub.expense.producer.CarStatusUpdateProducerService;
import com.autohub.expense.producer.FailedInvoiceDlqProducerService;
import com.autohub.expense.producer.InvoiceProducerService;
import com.autohub.expense.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceProcessorService {

    private final RevenueService revenueService;
    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final InvoiceProducerService invoiceProducerService;
    private final FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;
    private final InvoiceMapper invoiceMapper;

    public void processInvoice(Invoice existingInvoiceUpdated) {
        try {
            updateCarAndBooking(existingInvoiceUpdated);

            invoiceProducerService.sendMessage(invoiceMapper.mapEntityToDto(existingInvoiceUpdated));
            revenueService.addRevenue(existingInvoiceUpdated);
            log.info("Invoice with id: {} has been successfully closed", existingInvoiceUpdated.getId());
        } catch (Exception e) {
            log.error("Error while processing invoice with id: {}: {}", existingInvoiceUpdated.getId(), e.getMessage());
            processFailedInvoice(existingInvoiceUpdated);
        }
    }

    private void updateCarAndBooking(Invoice invoice) {
        carStatusUpdateProducerService.markCarAsAvailable(getCarUpdateDetails(invoice));
        closeBooking(invoice);
    }

    private void closeBooking(Invoice invoice) {
        Long bookingId = invoice.getBookingId();
        Long returnRentalOfficeId = invoice.getReturnRentalOfficeId();
        BookingClosingDetails bookingClosingDetails = getBookingClosingDetails(bookingId, returnRentalOfficeId);

        bookingUpdateProducerService.closeBooking(bookingClosingDetails);
    }

    private void processFailedInvoice(Invoice existingInvoiceUpdated) {
        InvoiceReprocessRequest invoiceReprocessRequest =
                invoiceMapper.mapToInvoiceReprocessRequest(existingInvoiceUpdated);

        failedInvoiceDlqProducerService.sendMessage(invoiceReprocessRequest);

        log.warn("Invoice with id: {} has failed to close, storing it to DLQ", existingInvoiceUpdated.getId());
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoice) {
        return CarUpdateDetails.builder()
                .carId(invoice.getCarId())
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId())
                .carState(invoice.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .build();
    }

    private BookingClosingDetails getBookingClosingDetails(Long bookingId, Long returnRentalOfficeId) {
        return BookingClosingDetails.builder()
                .bookingId(bookingId)
                .returnRentalOfficeId(returnRentalOfficeId)
                .build();
    }

}
