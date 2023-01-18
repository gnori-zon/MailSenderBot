package org.gnori.mailsenderbot.command;

import com.google.common.collect.ImmutableMap;
import org.gnori.mailsenderbot.command.commands.*;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.gnori.mailsenderbot.command.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    @Autowired
    public CommandContainer(SendBotMessageService sendBotMessageService,
                            ModifyDataBaseService modifyDataBaseService) {
        commandMap =  ImmutableMap.<String, Command>builder()
                .put(REGISTRATION.getCommandName(), new RegistrationCommand(sendBotMessageService))
                .put(BEGINNING.getCommandName(), new BeginningCommand(sendBotMessageService, modifyDataBaseService))
                .put(PROFILE.getCommandName(), new ProfileCommand(modifyDataBaseService, sendBotMessageService))
                .put(MAILING_HISTORY.getCommandName(), new MailingHistoryCommand())
                .put(CREATE_MAILING.getCommandName(), new CreateMailingCommand())
                .build();
        unknownCommand = new UnknownCommand(sendBotMessageService);
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
