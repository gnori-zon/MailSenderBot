package org.gnori.send.mail.worker.service.mail.recipinets.parser;

import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.shared.flow.Result;

import javax.mail.internet.InternetAddress;
import java.util.List;

public interface MailRecipientsParser {

    Result<InternetAddress[], MailFailure> parse(List<String> recipients);
}
