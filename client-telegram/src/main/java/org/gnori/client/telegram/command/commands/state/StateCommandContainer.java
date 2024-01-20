package org.gnori.client.telegram.command.commands.state;

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
public class StateCommandContainer implements CommandContainer<StateCommandType> {

    private final Map<StateCommandType, StateCommand> commandMap;

    public StateCommandContainer(List<StateCommand> commands) {

        commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(StateCommand::getSupportedType, Function.identity()));
    }

    @Override
    public void executeCommand(Account account, Update update) {

        commandMap.get(StateCommandType.of(account.getState())).
                execute(account, update);
    }

    @Override
    public CommandContainerType getSupportedType() {
        return CommandContainerType.STATE;
    }
}
