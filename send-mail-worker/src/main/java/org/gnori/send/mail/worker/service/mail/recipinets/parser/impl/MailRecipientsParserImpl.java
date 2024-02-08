package org.gnori.send.mail.worker.service.mail.recipinets.parser.impl;

import lombok.extern.log4j.Log4j2;
import org.gnori.send.mail.worker.service.mail.recipinets.parser.MailRecipientsParser;
import org.gnori.send.mail.worker.utils.MailUtils;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.stream.Stream;

@Log4j2
@Component
public class MailRecipientsParserImpl implements MailRecipientsParser {

    private static final int MAX_RECIPIENTS = 499;

    @Override
    public InternetAddress[] parse(List<String> recipients) {

        return recipients.stream()
                .limit(MAX_RECIPIENTS)
                .filter(MailUtils::validateMail)
                .flatMap(addressRaw -> {

                    try {
                        return Stream.of(new InternetAddress(addressRaw));
                    } catch (AddressException e) {
                        log.warn("unexpected state");
                        return Stream.empty();
                    }
                })
                .toArray(InternetAddress[]::new);
    }
}
