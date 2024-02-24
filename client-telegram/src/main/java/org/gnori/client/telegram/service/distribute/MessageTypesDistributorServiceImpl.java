package org.gnori.client.telegram.service.distribute;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.service.account.registrator.AccountRegistrator;
import org.gnori.client.telegram.service.command.CommandContainerType;
import org.gnori.client.telegram.service.command.CommandContainers;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
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

        final Account account = getRegisterAccount(update);
        distribute(account, update);
    }

    private Account getRegisterAccount(Update update) {

        return Optional.ofNullable(extractMessage(update))
                .flatMap(message -> accountRegistrator.getRegisterAccount(message.getChatId()))
                .orElseGet(() -> accountRegistrator.registrateBy(update));
    }

    private Message extractMessage(Update update) {

        if (update.hasCallbackQuery()) {

            return update.getCallbackQuery()
                    .getMessage();
        }

        return update.getMessage();
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
