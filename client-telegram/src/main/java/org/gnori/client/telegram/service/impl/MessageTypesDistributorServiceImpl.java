package org.gnori.client.telegram.service.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.CommandContainer;
import org.gnori.client.telegram.command.CommandType;
import org.gnori.client.telegram.service.AccountRegistrator;
import org.gnori.client.telegram.service.MessageTypesDistributorService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {

    private final CommandContainer commandContainer;
    private final AccountRegistrator accountRegistrator;

    @Override
    public void distribute(Update update) {

        getRegisterAccount(update)
                .ifPresent(account -> distribute(account, update));
    }

    private Optional<Account> getRegisterAccount(Update update) {

        return Optional.ofNullable(update.getMessage())
                .map(message ->
                        accountRegistrator.getRegisterAccount(message.getChatId())
                                .orElseGet(() -> accountRegistrator.registrateBy(update))
                );
    }

    private void distribute(Account account, Update update) {

        commandContainer.retriveCommand(detectCommandType(account, update))
                .execute(account, update);
    }


    private CommandType detectCommandType(
            @NonNull Account account,
            @NonNull Update update
    ) {

        if (!State.DEFAULT.equals(account.getState())) {
            return CommandType.STATE;
        }

        if (update.hasCallbackQuery()) {
            return CommandType.CALLBACK;
        }

        return Optional.ofNullable(update.getMessage())
                .flatMap(message -> {

                    if (message.hasDocument()) {
                        return Optional.of(CommandType.DOCUMENT);
                    }

                    if (message.hasPhoto()) {
                        return Optional.of(CommandType.PHOTO);
                    }

                    if (message.hasText()) {
                        return Optional.of(CommandType.TEXT);
                    }

                    return Optional.empty();
                })
                .orElse(CommandType.UNDEFINED);
    }
}
