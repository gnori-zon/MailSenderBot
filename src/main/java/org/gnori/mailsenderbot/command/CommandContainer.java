package org.gnori.mailsenderbot.command;

import com.google.common.collect.ImmutableMap;
import org.gnori.mailsenderbot.command.commands.*;
import org.gnori.mailsenderbot.command.commands.waiting_for_input.*;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.*;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Autowired;

import static org.gnori.mailsenderbot.command.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    @Autowired
    public CommandContainer(SendBotMessageService sendBotMessageService,
                            ModifyDataBaseService modifyDataBaseService,
                            MessageRepository messageRepository,
                            MailSenderService mailSenderService,
                            CryptoTool cryptoTool,
                            FileService fileService,
                            MessageTypesDistributorService messageTypesDistributorService) {
        commandMap =  ImmutableMap.<String, Command>builder()
                .put(REGISTRATION.getCommandName(), new RegistrationCommand(sendBotMessageService))
                .put(BACK.getCommandName(), new BackCommand(modifyDataBaseService, messageTypesDistributorService))
                .put(BEGINNING.getCommandName(), new BeginningCommand(sendBotMessageService, modifyDataBaseService))
                .put(PROFILE.getCommandName(), new ProfileCommand(modifyDataBaseService, sendBotMessageService))
                .put(MAILING_HISTORY.getCommandName(), new MailingHistoryCommand(modifyDataBaseService, sendBotMessageService))
                .put(CHANGE_KEY.getCommandName(), new BeforeChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CHANGE_MAIL.getCommandName(), new BeforeChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(KEY_FOR_MAIL_PENDING.getCommandName(), new AfterChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService,cryptoTool))
                .put(MAIL_PENDING.getCommandName(), new AfterChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CREATE_MAILING.getCommandName(), new CreateMailingCommand(sendBotMessageService, messageRepository))
                .put(CHANGE_ITEM.getCommandName(), new ChangeItemCommand(sendBotMessageService))
                .put(CHANGE_ITEM_TITLE.getCommandName(), new BeforeChangeTitleCommand(sendBotMessageService,modifyDataBaseService))
                .put(TITLE_PENDING.getCommandName(), new AfterChangeTitleCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CHANGE_ITEM_TEXT.getCommandName(), new BeforeChangeContentCommand(sendBotMessageService, modifyDataBaseService))
                .put(CONTENT_PENDING.getCommandName(), new AfterChangeContentCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CHANGE_ITEM_ANNEX.getCommandName(), new BeforeChangeAnnexCommand(sendBotMessageService,modifyDataBaseService))
                .put(ANNEX_PENDING.getCommandName(), new AfterChangeAnnexCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CHANGE_ITEM_RECIPIENTS.getCommandName(), new BeforeChangeRecipientsCommand(sendBotMessageService, modifyDataBaseService))
                .put(RECIPIENTS_PENDING.getCommandName(), new AfterChangeRecipientsCommand(modifyDataBaseService, sendBotMessageService, messageRepository))
                .put(CHANGE_ITEM_COUNT_FOR_RECIPIENTS.getCommandName(), new BeforeChangeCountForRecipientCommand(sendBotMessageService, modifyDataBaseService))
                .put(COUNT_FOR_RECIPIENT_PENDING.getCommandName(), new AfterChangeCountForRecipientCommand(modifyDataBaseService, sendBotMessageService, messageRepository))
                .put(SEND.getCommandName(), new SendCommand(sendBotMessageService, modifyDataBaseService))
                .put(SEND_ANONYMOUSLY.getCommandName(), new SendAnonymouslyCommand(sendBotMessageService,modifyDataBaseService,messageRepository, mailSenderService))
                .put(SEND_CURRENT_MAIL.getCommandName(), new SendCurrentMailCommand(sendBotMessageService,modifyDataBaseService,messageRepository,mailSenderService))
                .put(CLEAR_MESSAGE.getCommandName(), new ClearMessageCommand(sendBotMessageService,messageRepository))
                .put(DOWNLOAD_MESSAGE.getCommandName(), new BeforeDownloadMessageCommand(sendBotMessageService,modifyDataBaseService))
                .put(DOWNLOAD_MESSAGE_PENDING.getCommandName(), new AfterDownloadMessageCommand(sendBotMessageService,modifyDataBaseService, messageRepository, fileService ))
                .put(CHANGE_ITEM_SENT_DATE.getCommandName(), new BeforeChangeSentDateCommand(sendBotMessageService,modifyDataBaseService))
                .put(SENT_DATE_PENDING.getCommandName(), new AfterChangeSentDateCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(HELP.getCommandName(), new HelpCommand(sendBotMessageService))
                .build();
        unknownCommand = new UnknownCommand(sendBotMessageService);
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
