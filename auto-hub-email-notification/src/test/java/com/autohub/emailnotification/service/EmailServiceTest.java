package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.util.TestUtil;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MustacheFactory mustacheFactory;

    @Mock
    private Mustache mustache;

    @Mock
    private Writer writer;

    private MimeMessage realMimeMessage() {
        return new MimeMessage(jakarta.mail.Session.getInstance(new Properties()));
    }

    @Test
    void sendEmailTest_sendsMessageWithPdfAttachment() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ReflectionTestUtils.setField(emailService, "mailFrom", "noreply@autohub.com");
        ReflectionTestUtils.setField(emailService, "name", "Auto Hub");

        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);

        byte[] pdfBytes = "%PDF-1.4 content".getBytes(StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> emailService.sendEmail("test@email.com", invoiceResponse, pdfBytes));

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        // Transport.send(...) implicitly calls saveChanges() before transmitting, which is what
        // computes each body part's Content-Type header from its DataHandler. Our test doubles
        // out the real send, so we call it explicitly to inspect the message as it would actually
        // go out on the wire.
        sentMessage.saveChanges();
        String expectedFileName = "invoice-" + invoiceResponse.id() + ".pdf";

        assertInstanceOf(MimeMultipart.class, sentMessage.getContent());
        MimeMultipart multipart = (MimeMultipart) sentMessage.getContent();

        BodyPart attachmentPart = null;
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (expectedFileName.equals(part.getFileName())) {
                attachmentPart = part;
                break;
            }
        }

        if (attachmentPart == null) {
            fail("No attachment named '" + expectedFileName + "' found in the sent message");
        }

        assertNotNull(attachmentPart);
        assertTrue(attachmentPart.getContentType().toLowerCase().contains("application/pdf"));
    }

}
