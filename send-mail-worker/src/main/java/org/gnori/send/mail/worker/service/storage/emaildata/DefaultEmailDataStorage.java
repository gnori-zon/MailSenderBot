package org.gnori.send.mail.worker.service.storage.emaildata;

import org.gnori.send.mail.worker.model.EmailData;

import java.util.Optional;

public interface DefaultEmailDataStorage {
    Optional<EmailData> find();
    void add(EmailData email);
}
