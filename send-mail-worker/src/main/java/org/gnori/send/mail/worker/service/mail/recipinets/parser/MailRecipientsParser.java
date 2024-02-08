package org.gnori.send.mail.worker.service.mail.recipinets.parser;

import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.data.flow.Result;

import javax.mail.internet.InternetAddress;
import java.util.List;

public interface MailRecipientsParser {

    InternetAddress[] parse(List<String> recipients);
}
