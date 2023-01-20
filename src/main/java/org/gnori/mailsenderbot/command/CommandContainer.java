package org.gnori.mailsenderbot.command;

import com.google.common.collect.ImmutableMap;
import org.gnori.mailsenderbot.command.commands.*;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.gnori.mailsenderbot.command.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    @Autowired
    public CommandContainer(SendBotMessageService sendBotMessageService,
                            ModifyDataBaseService modifyDataBaseService,
                            MessageRepository messageRepository,
                            MessageTypesDistributorService messageTypesDistributorService) {
        commandMap =  ImmutableMap.<String, Command>builder()
                .put(REGISTRATION.getCommandName(), new RegistrationCommand(sendBotMessageService))
                .put(BACK.getCommandName(), new BackCommand(modifyDataBaseService, sendBotMessageService, messageTypesDistributorService))
                .put(BEGINNING.getCommandName(), new BeginningCommand(sendBotMessageService, modifyDataBaseService))
                .put(PROFILE.getCommandName(), new ProfileCommand(modifyDataBaseService, sendBotMessageService))
                .put(MAILING_HISTORY.getCommandName(), new MailingHistoryCommand(modifyDataBaseService, sendBotMessageService))
                .put(CHANGE_KEY.getCommandName(), new BeforeChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CHANGE_MAIL.getCommandName(), new BeforeChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(KEY_FOR_MAIL_PENDING.getCommandName(), new AfterChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(MAIL_PENDING.getCommandName(), new AfterChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CREATE_MAILING.getCommandName(), new CreateMailingCommand(sendBotMessageService, messageRepository))
                .put(CHANGE_ITEM.getCommandName(), new ChangeItemCommand(sendBotMessageService))
                .put(SEND.getCommandName(), new SendCommand(sendBotMessageService))
                .build();
        unknownCommand = new UnknownCommand(sendBotMessageService);
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
