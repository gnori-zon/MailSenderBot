package org.gnori.send.mail.worker.utils;

import org.gnori.send.mail.worker.service.mail.task.listener.impl.enums.MailDomain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-level testing for MailUtils")
class MailUtilsTest {

    @Test
    void successValidateMail() {

        final List<String> validEmails = List.of("asd13@gmail.com", "asdasSAD@yandex.ru", "41_asdeq@gmail.com");

        final int validEmailsCount = (int) validEmails.stream()
                .map(MailUtils::validateMail)
                .filter(isValid -> isValid)
                .count();

        Assertions.assertEquals(validEmails.size(), validEmailsCount);
    }

    @Test
    void failureValidateMail() {

        final List<String> invalidEmails = List.of("asd13.com", "asdasSAD@yandex", "@gmail.com");

        final int invalidEmailsCount = (int) invalidEmails.stream()
                .map(MailUtils::validateMail)
                .filter(isValid -> !isValid)
                .count();

        Assertions.assertEquals(invalidEmails.size(), invalidEmailsCount);
    }

    @Test
    void successDetectProperties() {

        final String mailPattern = "my_mail@%s.ru";

        final List<String> validEmailRaw = Arrays.stream(MailDomain.values())
                .flatMap(mailDomain -> mailDomain.getDomains().stream())
                .map(mailPattern::formatted)
                .toList();

        final int detectedPropertiesCount = (int) validEmailRaw.stream()
                .map(MailUtils::detectProperties)
                .filter(Optional::isPresent)
                .count();

        Assertions.assertEquals(validEmailRaw.size(), detectedPropertiesCount);
    }


    @Test
    void failureDetectProperties() {

        final List<String> invalidEmails = List.of("asd13.com", "asdasSAD@yx", "@gma.com");

        final int undetectedPropertiesCount = (int) invalidEmails.stream()
                .map(MailUtils::detectProperties)
                .filter(Optional::isEmpty)
                .count();

        Assertions.assertEquals(invalidEmails.size(), undetectedPropertiesCount);
    }
}