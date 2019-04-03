package com.example.notifier.services;


import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

@Slf4j
public class ExchangeEmailService implements EmailService{

    private final ExchangeService service =
            new ExchangeService(ExchangeVersion.Exchange2010_SP2);

    @Value("${mail.exchange.url:}")
    private String host;

    @Value("${mail.auth.username:}")
    private String username;

    @Value("${mail.auth.password:}")
    private String password;

    public void sendMail(String from, String to, String subject, String msg) {
        try {
            service.setCredentials(new WebCredentials(username, password));
            service.setUrl(new URI(host));
        }
        catch (Exception e) {
            log.error("Error trying to connect to exchange server.", e);
        }

        try {
            EmailMessage email = new EmailMessage(service);
            email.setSubject(subject);
            email.setBody(new MessageBody(msg));
            email.setFrom(new EmailAddress(from));
            email.getToRecipients().add(to);
            email.sendAndSaveCopy();
        }
        catch (Exception e) {
            log.error("Error sending the email.", e);
        }
    }

}
