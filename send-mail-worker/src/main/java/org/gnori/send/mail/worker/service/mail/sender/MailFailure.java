package org.gnori.send.mail.worker.service.mail.sender;

public enum MailFailure {
    MESSAGING_EXCEPTION,
    BAD_FILL_MESSAGE,
    ADDRESS_EXCEPTION,
    NOT_DELETE_FILE,
    BAD_LOAD_ANNEX,
    NO_FREE_MAILING_ADDRESSES,
    NOT_FOUND_EMAIL_ACCOUNT,
    BAD_DECRYPT_KEY
}
