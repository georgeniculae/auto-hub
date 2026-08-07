package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.util.Constants;
import com.autohub.exception.AutoHubException;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MustacheFactory mustacheFactory;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.name}")
    private String name;

    public void sendEmail(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailFrom, name);
            helper.setTo(toAddressEmail);
            helper.setSubject(Constants.SUBJECT);
            helper.setText(getMailBody(invoiceResponse), true);
            helper.addAttachment(getAttachmentName(invoiceResponse), new ByteArrayResource(pdfBytes),
                    Constants.APPLICATION_PDF_CONTENT_TYPE);

            mailSender.send(message);
        } catch (Exception e) {
            throw new AutoHubException("Error sending invoice email: " + e.getMessage());
        }
    }

    private String getAttachmentName(InvoiceResponse invoiceResponse) {
        return Constants.INVOICE_FILENAME_PREFIX + invoiceResponse.id() + Constants.PDF_EXTENSION;
    }

    private String getMailBody(Object object) {
        StringWriter stringWriter = new StringWriter();

        Mustache mustache = mustacheFactory.compile(
                Constants.MAIL_TEMPLATE_FOLDER + Constants.FILE_NAME + Constants.MUSTACHE_FORMAT);

        try {
            mustache.execute(stringWriter, object).flush();
        } catch (Exception e) {
            throw new AutoHubException("Error rendering mail body: " + e.getMessage());
        }

        return stringWriter.toString();
    }

}
