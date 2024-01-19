package org.gnori.client.telegram.command;

import org.gnori.store.entity.Account;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command<T> {

    void execute(Account account, Update update);

    T getSupportedType();
}
