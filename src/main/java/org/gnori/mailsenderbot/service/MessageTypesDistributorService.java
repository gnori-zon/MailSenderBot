package org.gnori.mailsenderbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageTypesDistributorService {
    void distributeMessageByType(Update update);
}
