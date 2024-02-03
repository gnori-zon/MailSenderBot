package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.command.commands.UndefinedCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UndefinedStateCommand implements StateCommand {

    private final UndefinedCommand undefinedCommand;

    @Override
    public void execute(Account account, Update update) {
        undefinedCommand.execute(account, update);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.UNDEFINED;
    }
}
