package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.util.Constants;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TemplateEngine templateEngine;

    public byte[] generatePdf(InvoiceResponse invoiceResponse) {
        Context context = new Context();
        context.setVariable(Constants.INVOICE, invoiceResponse);

        String html = templateEngine.process(Constants.INVOICE_PDF, context);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new AutoHubException("Error generating invoice PDF: " + e.getMessage());
        }
    }

}
