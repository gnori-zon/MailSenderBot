package org.gnori.client.telegram.command.commands.callback.impl.back.menustep.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommand;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.command.commands.text.impl.StartCommand;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartMenuStepCommand implements MenuStepCommand {

    private final StartCommand command;

    @Override
    public void execute(Account account, Update update) {
        command.execute(account, update);
    }

    @Override
    public MenuStepCommandType getSupportedType() {
        return MenuStepCommandType.START;
    }
}
