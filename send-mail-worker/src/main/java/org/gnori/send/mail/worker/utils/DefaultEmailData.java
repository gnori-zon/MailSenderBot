package org.gnori.send.mail.worker.utils;

import java.util.Optional;

public interface DefaultEmailData {
    Optional<EmailData> find();
    void add(EmailData email);
}
