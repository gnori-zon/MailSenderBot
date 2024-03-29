package org.gnori.send.mail.worker.service.mail.parser.recipinets;

import javax.mail.internet.InternetAddress;
import java.util.List;

@FunctionalInterface
public interface MailRecipientsParser {
    InternetAddress[] parse(List<String> recipients);
}
