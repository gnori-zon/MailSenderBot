package org.gnori.client.telegram.command.commands.text;

import org.gnori.client.telegram.command.CommandContainer;
import org.gnori.client.telegram.command.CommandContainerType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TextCommandContainer implements CommandContainer<TextCommandType> {

    private final Map<TextCommandType, TextCommand> commandMap;

    public TextCommandContainer(List<TextCommand> commands) {

        this.commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(TextCommand::getSupportedType, Function.identity()));
    }

    @Override
    public void executeCommand(Account account, Update update) {

        commandMap.get(TextCommandType.of(update.getMessage()))
                .execute(account, update);
    }

    @Override
    public CommandContainerType getSupportedType() {
        return CommandContainerType.TEXT;
    }
}
