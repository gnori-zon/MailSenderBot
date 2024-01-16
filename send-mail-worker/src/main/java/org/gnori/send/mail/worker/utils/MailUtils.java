package org.gnori.send.mail.worker.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gnori.send.mail.worker.service.mail.task.listener.impl.enums.MailDomain;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.awt.geom.Path2D.contains;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailUtils {

    private static final String VALID_EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String DOMAIN_SEPARATOR = "@";

    public static boolean validateMail(String emailAddress) {
        return emailAddress.matches(VALID_EMAIL_PATTERN);
    }

    public static Optional<Properties> detectProperties(String mail) {

        return Optional.ofNullable(mail)
                .map(MailUtils::extractDomainPart)
                .flatMap(MailUtils::findProperties);
    }

    private static Optional<Properties> findProperties(String domainPart) {

        for (final MailDomain domain: MailDomain.values()) {

            final String containsRegex = getContainsRegex(domain.getDomains());

            if (domainPart.matches(containsRegex)) {
                return Optional.of(domain.getProperties());
            }
        }

        return Optional.empty();
    }

    private static String extractDomainPart(@NotNull String mail) {

        final int indexSeparator = mail.indexOf(DOMAIN_SEPARATOR);
        
        return indexSeparator != -1
                ? mail.substring(indexSeparator)
                : "";
    }

    private static String getContainsRegex(Collection<String> elements) {

        return elements.stream()
                .collect(Collectors.joining("|", ".*[", "].*"));
    }
}
