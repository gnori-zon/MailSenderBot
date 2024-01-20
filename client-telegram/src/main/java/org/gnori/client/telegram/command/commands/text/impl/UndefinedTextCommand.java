package org.gnori.client.telegram.command.commands.text.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.UndefinedCommand;
import org.gnori.client.telegram.command.commands.text.TextCommand;
import org.gnori.client.telegram.command.commands.text.TextCommandType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UndefinedTextCommand implements TextCommand {

    private final UndefinedCommand undefinedCommand;

    @Override
    public void execute(Account account, Update update) {
        undefinedCommand.execute(account, update);
    }

    @Override
    public TextCommandType getSupportedType() {
        return TextCommandType.UNDEFINED;
    }
}
