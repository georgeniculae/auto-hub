package com.autohub.audit.service;

import com.autohub.audit.entity.BookingAuditLogInfo;
import com.autohub.audit.entity.CustomerAuditLogInfo;
import com.autohub.audit.entity.ExpenseAuditLogInfo;
import com.autohub.audit.mapper.AuditLogInfoMapper;
import com.autohub.audit.mapper.AuditLogInfoMapperImpl;
import com.autohub.audit.repository.BookingAuditLogInfoRepository;
import com.autohub.audit.repository.CustomerAuditLogInfoRepository;
import com.autohub.audit.repository.ExpenseAuditLogInfoRepository;
import com.autohub.audit.util.TestUtil;
import com.autohub.dto.common.AuditLogInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingAuditLogInfoServiceTest {

    @InjectMocks
    private AuditLogInfoService auditLogInfoService;

    @Mock
    private BookingAuditLogInfoRepository bookingAuditLogInfoRepository;

    @Mock
    private CustomerAuditLogInfoRepository customerAuditLogInfoRepository;

    @Mock
    private ExpenseAuditLogInfoRepository expenseAuditLogInfoRepository;

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void saveBookingAuditLogInfoTest_success() {
        BookingAuditLogInfo bookingAuditLogInfo =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfo.json", BookingAuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(bookingAuditLogInfoRepository.save(any(BookingAuditLogInfo.class))).thenReturn(bookingAuditLogInfo);

        assertDoesNotThrow(() -> auditLogInfoService.saveBookingAuditLogInfo(auditLogInfoRequest));

        verify(auditLogInfoMapper).mapDtoToBookingEntity(any(AuditLogInfoRequest.class));
    }

    @Test
    void saveCustomerAuditLogInfoTest_success() {
        CustomerAuditLogInfo customerAuditLogInfo =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfo.json", CustomerAuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(customerAuditLogInfoRepository.save(any(CustomerAuditLogInfo.class))).thenReturn(customerAuditLogInfo);

        assertDoesNotThrow(() -> auditLogInfoService.saveCustomerAuditLogInfo(auditLogInfoRequest));

        verify(auditLogInfoMapper).mapDtoToCustomerEntity(any(AuditLogInfoRequest.class));
    }

    @Test
    void saveExpenseAuditLogInfoTest_success() {
        ExpenseAuditLogInfo expenseAuditLogInfo =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfo.json", ExpenseAuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(expenseAuditLogInfoRepository.save(any(ExpenseAuditLogInfo.class))).thenReturn(expenseAuditLogInfo);

        assertDoesNotThrow(() -> auditLogInfoService.saveExpenseAuditLogInfo(auditLogInfoRequest));

        verify(auditLogInfoMapper).mapDtoToExpenseEntity(any(AuditLogInfoRequest.class));
    }

}
