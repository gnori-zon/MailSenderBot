package org.gnori.send.mail.worker.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultEmailData {

    @Value("${base.mails}")
    private String baseMailsLogins;

    @Value("${base.keys}")
    private String baseMailsPasswords;

    private final ConcurrentLinkedQueue<EmailData> emailData = new ConcurrentLinkedQueue<>();

    @PostConstruct
    private void init(){

        final List<String> logins = Arrays.stream(baseMailsLogins.split(",")).map(String::trim).toList();
        final List<String> passwords = Arrays.stream(baseMailsPasswords.split(",")).map(String::trim).toList();

        for(int i = 0; i < logins.size();i++){
            emailData.add(new EmailData(logins.get(i),passwords.get(i)));
        }
    }

    public Optional<EmailData> find() {
        return Optional.ofNullable(emailData.poll());
    }

    public void add(EmailData email) {

        Optional.ofNullable(email)
                .ifPresent(emailData::add);
    }
}
