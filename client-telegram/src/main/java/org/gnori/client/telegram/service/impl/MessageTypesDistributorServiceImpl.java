package org.gnori.client.telegram.service.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.CommandContainers;
import org.gnori.client.telegram.command.CommandContainerType;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
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

    private final CommandContainers commandContainers;
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

        commandContainers.retriveCommandContainer(detectCommandType(account, update))
                .executeCommand(account, update);
    }


    private CommandContainerType detectCommandType(
            @NonNull Account account,
            @NonNull Update update
    ) {

        if (CallbackCommandType.isBackCommand(update)) {
            return CommandContainerType.CALLBACK;
        }

        if (!State.DEFAULT.equals(account.getState())) {
            return CommandContainerType.STATE;
        }

        if (update.hasCallbackQuery()) {
            return CommandContainerType.CALLBACK;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            return CommandContainerType.TEXT;
        }

        return CommandContainerType.UNDEFINED;
    }
}
