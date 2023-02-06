package org.gnori.mailsenderbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Service for distributing messages by type
 */
public interface MessageTypesDistributorService {
    void distributeMessageByType(Update update);
}
