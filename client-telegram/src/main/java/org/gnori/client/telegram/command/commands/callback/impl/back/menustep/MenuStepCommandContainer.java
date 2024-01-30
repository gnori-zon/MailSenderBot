package org.gnori.client.telegram.command.commands.callback.impl.back.menustep;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MenuStepCommandContainer {
    private final Map<MenuStepCommandType, MenuStepCommand> commandMap;

    public MenuStepCommandContainer(List<MenuStepCommand> commands) {

        this.commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(MenuStepCommand::getSupportedType, Function.identity()));
    }

    public MenuStepCommand get(MenuStepCommandType commandType) {
        return commandMap.get(commandType);
    }
}
