package org.gnori.send.mail.worker.service.mail.recipinets.parser.impl;

import lombok.extern.log4j.Log4j2;
import org.gnori.send.mail.worker.service.mail.recipinets.parser.MailRecipientsParser;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.send.mail.worker.utils.MailUtils;
import org.gnori.shared.flow.Result;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MailRecipientsParserImpl implements MailRecipientsParser {

    private static final int MAX_RECIPIENTS = 499;

    @Override
    public Result<InternetAddress[], MailFailure> parse(List<String> recipients) {

        try {

        final String recipientsStr = recipients.stream()
                .limit(MAX_RECIPIENTS)
                .filter(MailUtils::validateMail)
                .collect(Collectors.joining(","));


            return Result.success(InternetAddress.parse(recipientsStr));
        } catch (AddressException e) {
            log.error("bad address: {}", e.getLocalizedMessage());
            return Result.failure(MailFailure.ADDRESS_EXCEPTION);
        }
    }
}
