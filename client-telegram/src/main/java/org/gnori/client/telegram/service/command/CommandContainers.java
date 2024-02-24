package org.gnori.client.telegram.service.command;


import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandContainers {

    private final Map<CommandContainerType, CommandContainer<?>> commandContainerMap;

    public CommandContainers(List<CommandContainer<?>> commandContainers) {

        commandContainerMap = commandContainers.stream()
                .collect(Collectors.toMap(CommandContainer::getSupportedType, Function.identity()));
    }

    public @NotNull CommandContainer<?> retriveCommandContainer(CommandContainerType commandContainerType) {
        return commandContainerMap.get(commandContainerType);
    }
}
