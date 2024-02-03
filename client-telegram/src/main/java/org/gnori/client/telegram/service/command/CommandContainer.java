package org.gnori.client.telegram.service.command;

import org.gnori.store.entity.Account;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandContainer<T> {

    void executeCommand(Account account, Update update);
    CommandContainerType getSupportedType();
}
