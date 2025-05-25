package com.autohub.expense.service;

import com.autohub.dto.common.BookingResponse;
import com.autohub.dto.common.InvoiceResponse;
import com.autohub.dto.expense.InvoiceRequest;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.expense.entity.Invoice;
import com.autohub.expense.mapper.InvoiceMapper;
import com.autohub.expense.mapper.InvoiceMapperImpl;
import com.autohub.expense.repository.InvoiceRepository;
import com.autohub.expense.util.AssertionUtil;
import com.autohub.expense.util.TestUtil;
import com.autohub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllInvoices()).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoiceResponses = invoiceService.findAllInvoices();
        AssertionUtil.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findAllActive()).thenReturn(Stream.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findAllInvoices());
        List<InvoiceResponse> allActiveInvoices = invoiceService.findAllActiveInvoices();

        AssertionUtil.assertInvoiceResponse(invoice, allActiveInvoices.getFirst());
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));

        assertDoesNotThrow(() -> invoiceService.findInvoiceById(1L));
        InvoiceResponse invoiceResponse = invoiceService.findInvoiceById(1L);

        AssertionUtil.assertInvoiceResponse(invoice, invoiceResponse);
    }

    @Test
    void findInvoiceByIdTest_errorOnFindingById() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> invoiceService.findInvoiceById(1L));

        assertNotNull(autoHubNotFoundException);
        assertEquals("Invoice with id 1 does not exist", autoHubNotFoundException.getReason());
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByCommentsIgnoreCase(anyString())).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoiceResponses = invoiceService.findInvoiceByComments("comment");
        AssertionUtil.assertInvoiceResponse(invoice, invoiceResponses.getFirst());
    }

    @Test
    void findAllInvoicesByCustomerIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Stream.of(invoice));

        List<InvoiceResponse> invoices = invoiceService.findAllInvoicesByCustomerUsername("user");

        AssertionUtil.assertInvoiceResponse(invoice, invoices.getFirst());
    }

    @Test
    void saveInvoice_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.saveInvoice(bookingResponse));
    }

    @Test
    void saveInvoice_error_existingInvoice() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(invoiceRepository.existsByBookingId(anyLong())).thenReturn(true);

        AutoHubResponseStatusException exception =
                assertThrows(AutoHubResponseStatusException.class, () -> invoiceService.saveInvoice(bookingResponse));

        assertThat(exception.getMessage()).contains("Invoice already exists");
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(anyLong())).thenReturn(Optional.ofNullable(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        assertDoesNotThrow(() -> invoiceService.updateInvoiceAfterBookingUpdate(bookingResponse));
    }

    @Test
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        Invoice closedInvoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(closedInvoice);

        InvoiceResponse invoiceResponse = invoiceService.closeInvoice(1L, invoiceRequest);
        assertNotNull(invoiceResponse);

        verify(invoiceMapper).mapEntityToDto(any(Invoice.class));
    }

}
