package org.gnori.send.mail.worker.service.mail.task.listener;

import org.gnori.data.model.Message;

/**
 * Service for sending mailing
 */
public interface MailTaskListener {
    void receivedMailTask(Message message);
}
