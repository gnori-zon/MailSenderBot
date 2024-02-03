package org.gnori.client.telegram.service.command.commands.callback;

import org.gnori.client.telegram.service.command.CommandContainer;
import org.gnori.client.telegram.service.command.CommandContainerType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CallbackCommandContainer implements CommandContainer<CallbackCommandType> {

    private final Map<CallbackCommandType, CallbackCommand> commandMap;

    public CallbackCommandContainer(List<CallbackCommand> commands) {

        commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(CallbackCommand::getSupportedType, Function.identity()));
    }

    @Override
    public void executeCommand(Account account, Update update) {

        commandMap.get(CallbackCommandType.of(update.getCallbackQuery()))
                .execute(account, update);
    }

    @Override
    public CommandContainerType getSupportedType() {
        return CommandContainerType.CALLBACK;
    }
}
