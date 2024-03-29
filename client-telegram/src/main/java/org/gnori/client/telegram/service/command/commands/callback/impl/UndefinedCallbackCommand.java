package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.command.commands.UndefinedCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.data.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UndefinedCallbackCommand implements CallbackCommand {

    private final UndefinedCommand undefinedCommand;

    @Override
    public void execute(Account account, Update update) {
        undefinedCommand.execute(account, update);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.UNDEFINED;
    }
}
