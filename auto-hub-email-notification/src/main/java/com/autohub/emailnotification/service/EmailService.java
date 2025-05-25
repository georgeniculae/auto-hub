package com.autohub.emailnotification.service;

import com.autohub.emailnotification.util.Constants;
import com.autohub.exception.AutoHubException;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGrid;
    private final MustacheFactory mustacheFactory;

    @Value("${sendgrid.mail.from}")
    private String mailFrom;

    @Value("${sendgrid.mail.name}")
    private String name;

    public Response sendEmail(Mail mail) {
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint(Constants.ENDPOINT);

        try {
            request.setBody(mail.build());

            return sendGrid.api(request);
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }
    }

    public Mail createMail(String toAddressEmail, Object object) {
        Email from = new Email(mailFrom, name);
        Email to = new Email(toAddressEmail);

        Content content = new Content(Constants.CONTENT_TYPE, getMailBody(object));

        Mail mail = new Mail(from, Constants.SUBJECT, to, content);
        mail.setSubject(Constants.SUBJECT);

        return mail;
    }

    private String getMailBody(Object object) {
        StringWriter stringWriter = new StringWriter();

        Mustache mustache = mustacheFactory.compile(Constants.MAIL_TEMPLATE_FOLDER + Constants.FILE_NAME + Constants.MUSTACHE_FORMAT);
        try {
            mustache.execute(stringWriter, object).flush();
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }

        return stringWriter.toString();
    }

}
