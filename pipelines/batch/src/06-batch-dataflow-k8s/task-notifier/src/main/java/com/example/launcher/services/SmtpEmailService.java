package com.example.launcher.services;


import com.example.launcher.properties.SmtpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Slf4j
public class SmtpEmailService implements EmailService{

    @Autowired
    private SmtpProperties smtpProperties;

    @Value("${mail.username:}")
    private String username;

    @Value("${mail.password:}")
    private String password;

    public void sendMail(String from, String to, String subject, String msg) {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", String.valueOf(smtpProperties.isAuth()));
        prop.put("mail.smtp.starttls.enable", String.valueOf(smtpProperties.isStarttlsEnabled()));
        prop.put("mail.smtp.host", smtpProperties.getHost());
        prop.put("mail.smtp.port", smtpProperties.getPort());
        prop.put("mail.smtp.ssl.trust", smtpProperties.getSsltrust());

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
