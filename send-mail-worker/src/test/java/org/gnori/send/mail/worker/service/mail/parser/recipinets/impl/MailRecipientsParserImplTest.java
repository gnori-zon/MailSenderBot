package org.gnori.send.mail.worker.service.mail.parser.recipinets.impl;

import org.gnori.send.mail.worker.service.mail.parser.recipinets.MailRecipientsParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

class MailRecipientsParserImplTest {

    private static final int MAX_RECIPIENTS = 499;

    private static final List<String> GOOD_RECIPIENTS = List.of(
            "as99dad@gmail.com",
            "asd2ad@yandex.ru",
            "123asd23@mail.ru",
            "ad_asd23@gmail.com",
            "adasdASDASD@yandex.ru",
            "LKOojv@doom.yandex.ru"
    );

    private static final List<String> FORBIDDEN_SIGNS = List.of("$", "*", "#", "%", "^");

    private final MailRecipientsParser parser = new MailRecipientsParserImpl();

    @Test
    void successParseGoodAddress() {

        final List<String> recipientsRaws = generateRecipients(false, randomInt(100, 1000));
        final InternetAddress[] addresses = parser.parse(recipientsRaws);

        Assertions.assertEquals(Math.min(MAX_RECIPIENTS, recipientsRaws.size()), addresses.length);
    }

    @Test
    void successParseWithBadAddressAfterLimit() {

        final List<String> recipientsRaws = new ArrayList<>(generateRecipients(false, randomInt(500, 1000)));
        recipientsRaws.add(generateBadRecipient());
        recipientsRaws.add(null);

        final InternetAddress[] addresses = parser.parse(recipientsRaws);
        Assertions.assertEquals(Math.min(MAX_RECIPIENTS, recipientsRaws.size()), addresses.length);
    }

    @Test
    void successParseWithSkipBadAddress() {

        final List<String> recipientsRaws = generateRecipients(true, randomInt(100, 499));

        final InternetAddress[] addresses = parser.parse(recipientsRaws);
        Assertions.assertTrue(recipientsRaws.size() > addresses.length);
    }

    private List<String> generateRecipients(boolean withInvalidRecipients, int count) {

        if (withInvalidRecipients && count == 0) {
            throw new RuntimeException("Illegal argument exception: withInvalidRecipients == true and count == 0");
        }

        return IntStream.range(0, count)
                .mapToObj(index ->
                        withInvalidRecipients && index % 2 == 0
                                ? generateBadRecipient()
                                : getRandomGoodRecipient()
                )
                .toList();
    }

    private String getRandomGoodRecipient() {

        final List<String> goodRecipients = new ArrayList<>(GOOD_RECIPIENTS);
        Collections.shuffle(goodRecipients, new Random());

        return goodRecipients.stream()
                .findFirst()
                .orElseThrow();
    }

    private String generateBadRecipient() {

        if (randomBoolean()) {
            return null;
        }

        final String mainPart = "main_mail2part";
        final String atSign = "@";
        final String point = ".";
        final String domainFirst = "gmail";
        final String domainSecond = "com";

        final String forbiddenSign = getRandomForbiddenSign();

        final boolean withoutAtSign = randomBoolean();
        final boolean withoutPoint = randomBoolean();
        final boolean withoutDomainFirst = randomBoolean();
        final boolean withoutDomainSecond = randomBoolean();
        boolean withoutForbiddenSign = randomBoolean();

        if (!withoutAtSign && !withoutPoint && !withoutDomainFirst && !withoutDomainSecond && withoutForbiddenSign) {
            withoutForbiddenSign = false;
        }

        final String atSignPart = withoutAtSign
                ? emptyString()
                : atSign;

        final String pointPart = withoutPoint
                ? emptyString()
                : point;

        final String domainFirstPart = withoutDomainFirst
                ? emptyString()
                : domainFirst;

        final String domainSecondPart = withoutDomainSecond
                ? emptyString()
                : domainSecond;

        final String forbiddenPart = withoutForbiddenSign
                ? emptyString()
                : forbiddenSign;

        final String secondForbiddenPart = withoutForbiddenSign
                ? ")"
                : "(";

        return "%s%s%s%s%s%s%s".formatted(
                mainPart,
                forbiddenPart,
                atSignPart,
                secondForbiddenPart,
                domainFirstPart,
                pointPart,
                domainSecondPart
        );
    }

    private String emptyString() {
        return "";
    }

    private String getRandomForbiddenSign() {

        final List<String> forbiddenSigns = new ArrayList<>(FORBIDDEN_SIGNS);
        Collections.shuffle(forbiddenSigns, new Random());

        return forbiddenSigns.stream()
                .findFirst()
                .orElseThrow();
    }

    private boolean randomBoolean() {
        return new Random().nextBoolean();
    }

    private int randomInt(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }
}