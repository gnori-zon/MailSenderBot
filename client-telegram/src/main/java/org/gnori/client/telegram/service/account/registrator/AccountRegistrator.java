package org.gnori.client.telegram.service.account.registrator;

import org.gnori.data.entity.Account;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface AccountRegistrator {

    Optional<Account> getRegisterAccount(Long chatId);
    Account registrateBy(Update update);
}
