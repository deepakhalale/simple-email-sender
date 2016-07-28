package com.locusintellect;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

    private final String smtpHost;
    private final int smtpPort;
    private final String fromEmailAddress;
    private final String toEmailAddress;
    private final String replyEmailAddress;
    private final String emailSubject;
    private final String emailMessage;

    @Autowired
    public EmailSender(@Value("${smtp.host}") final String smtpHost, @Value("${smtp.port}") final int smtpPort,
                       @Value("${from.email}") final String fromEmailAddress, @Value("${to.email}") final String toEmailAddress,
                       @Value("${reply.email}") final String replyEmailAddress, @Value("${email.subject}") final String emailSubject,
                       @Value("${email.message}") final String emailMessage) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.fromEmailAddress = fromEmailAddress;
        this.toEmailAddress = toEmailAddress;
        this.replyEmailAddress = replyEmailAddress;
        this.emailSubject = emailSubject;
        this.emailMessage = emailMessage;
    }

    public void send() {
        validateSmtpProperties(smtpHost, smtpPort);
        validateEmailAddresses(fromEmailAddress, toEmailAddress, replyEmailAddress);
        validateEmailData(emailSubject, emailMessage);
        LOG.info("About to send an email to {}.", toEmailAddress);

        final Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.auth", "false");
        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.port", smtpPort);

        final Session session = Session.getInstance(smtpProperties, null);

        try {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmailAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
            message.setReplyTo(InternetAddress.parse(replyEmailAddress));
            message.setSubject(emailSubject);
            message.setText(emailMessage);

            Transport.send(message);
            LOG.info("Successfully sent email to {}.", toEmailAddress);

        } catch (MessagingException e) {
            throw new FailToSendEmailException("Error sending email. ", e);
        }
    }

    private void validateSmtpProperties(final String smtpHost, final int smtpPort) {
        if (Strings.isNullOrEmpty(smtpHost) || smtpHost.trim().isEmpty()) {
            LOG.error("Property smtp.host is either null or empty.");
            throw new MissingRequiredPropertiesException("Property smtp.host is either null or empty.");
        }
        if (smtpPort <= 0) {
            LOG.error("Invalid value for property smtp.port.");
            throw new MissingRequiredPropertiesException("Invalid value for property smtp.port.");
        }
    }

    private void validateEmailAddresses(final String fromEmailAddress, final String toEmailAddress, final String replyEmailAddress) {
        if (Strings.isNullOrEmpty(fromEmailAddress) || fromEmailAddress.trim().isEmpty()) {
            LOG.error("Property from.email is either null or empty.");
            throw new MissingRequiredPropertiesException("Property from.email is either null or empty.");
        }
        if (Strings.isNullOrEmpty(toEmailAddress) || toEmailAddress.trim().isEmpty()) {
            LOG.error("Property to.email is either null or empty.");
            throw new MissingRequiredPropertiesException("Property to.email is either null or empty.");
        }
        if (Strings.isNullOrEmpty(replyEmailAddress) || replyEmailAddress.trim().isEmpty()) {
            LOG.error("Property reply.email is either null or empty.");
            throw new MissingRequiredPropertiesException("Property reply.email is either null or empty.");
        }
    }

    private void validateEmailData(final String emailSubject, final String emailMessage) {
        if (Strings.isNullOrEmpty(emailSubject) || emailSubject.trim().isEmpty()) {
            LOG.error("Property email.subject is either null or empty.");
            throw new MissingRequiredPropertiesException("Property email.subject is either null or empty.");
        }
        if (Strings.isNullOrEmpty(emailMessage) || emailMessage.trim().isEmpty()) {
            LOG.error("Property email.message is either null or empty.");
            throw new MissingRequiredPropertiesException("Property email.message is either null or empty.");
        }
    }
}
