package org.gnori.send.mail.worker.service.mail.task.listener.impl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Properties;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum MailDomain {

    YANDEX(createYandexProperties(), Set.of("ya", "yandex")),
    GMAIL(createGmailProperties(), Set.of("gmail")),
    MAIL(createMailProperties(), Set.of("mail", "inbox", "bk", "list", "internet"));

    private final Properties properties;
    private final Set<String> domains;

    private static Properties createGmailProperties() {

        final Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return props;
    }

    private static Properties createMailProperties() {

        final Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.mail.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "true");

        return props;
    }

    private static Properties createYandexProperties() {

        final Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "true");

        return props;
    }
}
