package org.gnori.client.telegram.command;


import com.google.common.collect.ImmutableMap;
import org.gnori.client.telegram.command.commands.BackCommand;
import org.gnori.client.telegram.command.commands.BeginningCommand;
import org.gnori.client.telegram.command.commands.ChangeItemCommand;
import org.gnori.client.telegram.command.commands.ClearMessageCommand;
import org.gnori.client.telegram.command.commands.CreateMailingCommand;
import org.gnori.client.telegram.command.commands.HelpCommand;
import org.gnori.client.telegram.command.commands.MailingHistoryCommand;
import org.gnori.client.telegram.command.commands.ProfileCommand;
import org.gnori.client.telegram.command.commands.RegistrationCommand;
import org.gnori.client.telegram.command.commands.SendAnonymouslyCommand;
import org.gnori.client.telegram.command.commands.SendCommand;
import org.gnori.client.telegram.command.commands.SendCurrentMailCommand;
import org.gnori.client.telegram.command.commands.UnknownCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeAnnexCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeContentCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeCountForRecipientCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeKeyForMailCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeMailCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeRecipientsCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeSentDateCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeTitleCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.AfterDownloadMessageCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeAnnexCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeContentCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeCountForRecipientCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeKeyForMailCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeMailCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeRecipientsCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeSentDateCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeChangeTitleCommand;
import org.gnori.client.telegram.command.commands.waiting_for_input.BeforeDownloadMessageCommand;
import org.gnori.client.telegram.service.FileService;
import org.gnori.client.telegram.service.MessageTypesDistributorService;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    @Autowired
    public CommandContainer(SendBotMessageService sendBotMessageService,
                            ModifyDataBaseService modifyDataBaseService,
                            MessageRepositoryService messageRepository,
                            RabbitTemplate rabbitTemplate,
                            CryptoTool cryptoTool,
                            FileService fileService,
                            String exchangeName,
                            MessageTypesDistributorService messageTypesDistributorService) {
        commandMap =  ImmutableMap.<String, Command>builder()
                .put(CommandName.REGISTRATION.getCommandName(), new RegistrationCommand(sendBotMessageService))
                .put(CommandName.BACK.getCommandName(), new BackCommand(modifyDataBaseService, messageTypesDistributorService))
                .put(CommandName.BEGINNING.getCommandName(), new BeginningCommand(sendBotMessageService, modifyDataBaseService))
                .put(CommandName.PROFILE.getCommandName(), new ProfileCommand(modifyDataBaseService, sendBotMessageService))
                .put(CommandName.MAILING_HISTORY.getCommandName(), new MailingHistoryCommand(modifyDataBaseService, sendBotMessageService))
                .put(CommandName.CHANGE_KEY.getCommandName(), new BeforeChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CommandName.CHANGE_MAIL.getCommandName(), new BeforeChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(
                    CommandName.KEY_FOR_MAIL_PENDING.getCommandName(), new AfterChangeKeyForMailCommand(modifyDataBaseService, sendBotMessageService,cryptoTool))
                .put(CommandName.MAIL_PENDING.getCommandName(), new AfterChangeMailCommand(modifyDataBaseService, sendBotMessageService))
                .put(CommandName.CREATE_MAILING.getCommandName(), new CreateMailingCommand(sendBotMessageService, messageRepository))
                .put(CommandName.CHANGE_ITEM.getCommandName(), new ChangeItemCommand(sendBotMessageService))
                .put(CommandName.CHANGE_ITEM_TITLE.getCommandName(), new BeforeChangeTitleCommand(sendBotMessageService,modifyDataBaseService))
                .put(CommandName.TITLE_PENDING.getCommandName(), new AfterChangeTitleCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CommandName.CHANGE_ITEM_TEXT.getCommandName(), new BeforeChangeContentCommand(sendBotMessageService, modifyDataBaseService))
                .put(CommandName.CONTENT_PENDING.getCommandName(), new AfterChangeContentCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CommandName.CHANGE_ITEM_ANNEX.getCommandName(), new BeforeChangeAnnexCommand(sendBotMessageService,modifyDataBaseService))
                .put(CommandName.ANNEX_PENDING.getCommandName(), new AfterChangeAnnexCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(
                    CommandName.CHANGE_ITEM_RECIPIENTS.getCommandName(), new BeforeChangeRecipientsCommand(sendBotMessageService, modifyDataBaseService))
                .put(
                    CommandName.RECIPIENTS_PENDING.getCommandName(), new AfterChangeRecipientsCommand(modifyDataBaseService, sendBotMessageService, messageRepository))
                .put(
                    CommandName.CHANGE_ITEM_COUNT_FOR_RECIPIENTS.getCommandName(), new BeforeChangeCountForRecipientCommand(sendBotMessageService, modifyDataBaseService))
                .put(
                    CommandName.COUNT_FOR_RECIPIENT_PENDING.getCommandName(), new AfterChangeCountForRecipientCommand(modifyDataBaseService, sendBotMessageService, messageRepository))
                .put(CommandName.SEND.getCommandName(), new SendCommand(sendBotMessageService, modifyDataBaseService))
                .put(CommandName.SEND_ANONYMOUSLY.getCommandName(), new SendAnonymouslyCommand(sendBotMessageService,modifyDataBaseService,messageRepository, rabbitTemplate, exchangeName))
                .put(CommandName.SEND_CURRENT_MAIL.getCommandName(), new SendCurrentMailCommand(sendBotMessageService,modifyDataBaseService,messageRepository,rabbitTemplate, exchangeName))
                .put(CommandName.CLEAR_MESSAGE.getCommandName(), new ClearMessageCommand(sendBotMessageService,messageRepository))
                .put(
                    CommandName.DOWNLOAD_MESSAGE.getCommandName(), new BeforeDownloadMessageCommand(sendBotMessageService,modifyDataBaseService))
                .put(
                    CommandName.DOWNLOAD_MESSAGE_PENDING.getCommandName(), new AfterDownloadMessageCommand(sendBotMessageService,modifyDataBaseService, messageRepository, fileService ))
                .put(
                    CommandName.CHANGE_ITEM_SENT_DATE.getCommandName(), new BeforeChangeSentDateCommand(sendBotMessageService,modifyDataBaseService))
                .put(CommandName.SENT_DATE_PENDING.getCommandName(), new AfterChangeSentDateCommand(modifyDataBaseService,sendBotMessageService,messageRepository))
                .put(CommandName.HELP.getCommandName(), new HelpCommand(sendBotMessageService))
                .build();
        unknownCommand = new UnknownCommand(sendBotMessageService);
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
