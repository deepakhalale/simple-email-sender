package com.locusintellect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EmailSenderApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderApplication.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(EmailSenderApplication.class, args);

        LOG.info("Simple Email Sender Application started.");

        final EmailSender emailSender = applicationContext.getBean(EmailSender.class);
        emailSender.send();
        applicationContext.close();
    }
}
