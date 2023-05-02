package org.gnori.client.telegram.service.impl;


import static org.gnori.client.telegram.command.CommandName.ANNEX_PENDING;
import static org.gnori.client.telegram.command.CommandName.CONTENT_PENDING;
import static org.gnori.client.telegram.command.CommandName.COUNT_FOR_RECIPIENT_PENDING;
import static org.gnori.client.telegram.command.CommandName.DOWNLOAD_MESSAGE_PENDING;
import static org.gnori.client.telegram.command.CommandName.KEY_FOR_MAIL_PENDING;
import static org.gnori.client.telegram.command.CommandName.MAIL_PENDING;
import static org.gnori.client.telegram.command.CommandName.RECIPIENTS_PENDING;
import static org.gnori.client.telegram.command.CommandName.REGISTRATION;
import static org.gnori.client.telegram.command.CommandName.SENT_DATE_PENDING;
import static org.gnori.client.telegram.command.CommandName.TITLE_PENDING;

import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.CommandContainer;
import org.gnori.client.telegram.service.FileService;
import org.gnori.client.telegram.service.MessageTypesDistributorService;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Implementation service {@link MessageTypesDistributorService}
 */
@Log4j2
@Service
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {

    private final CommandContainer commandContainer;
    private final ModifyDataBaseService modifyDataBaseService;
    public MessageTypesDistributorServiceImpl(SendBotMessageService sendBotMessageService,
                                              ModifyDataBaseService modifyDataBaseService,
                                              MessageRepositoryService messageRepository,
                                              RabbitTemplate rabbitTemplate,
                                              FileService fileService,
                                              CryptoTool cryptoTool) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.commandContainer = new CommandContainer(sendBotMessageService,
                modifyDataBaseService,
                messageRepository,
                rabbitTemplate,
                cryptoTool,
                fileService,
                this);
    }

    @Override
    public void distributeMessageByType(Update update) {
            var message = update.getMessage();

            if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else if (message.hasDocument()) {
                processDocMessage(update);
            } else if (message.hasPhoto()) {
                processPhotoMessage(update);
            } else if (message.hasText()) {
                processTextMessage(update);
            }else {
                processUnsupportedMessageTypeView(update);
            }

    }

    private void processCallbackQuery(Update update) {
        //TODO may be add findAccountDTOById and check register
        var command = update.getCallbackQuery().getData();
        commandContainer.retrieveCommand(command).execute(update);
    }

    private void processTextMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account.isPresent()){
            if(State.KEY_FOR_MAIL_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(KEY_FOR_MAIL_PENDING.getCommandName()).execute(update);
            }else if(State.MAIL_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(MAIL_PENDING.getCommandName()).execute(update);
            }else if(State.TITLE_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(TITLE_PENDING.getCommandName()).execute(update);
            }else if(State.CONTENT_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(CONTENT_PENDING.getCommandName()).execute(update);
            }else if(State.RECIPIENTS_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(RECIPIENTS_PENDING.getCommandName()).execute(update);
            }else if(State.COUNT_FOR_RECIPIENT_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(COUNT_FOR_RECIPIENT_PENDING.getCommandName()).execute(update);
            }else if(State.SENT_DATE_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(SENT_DATE_PENDING.getCommandName()).execute(update);
            }else{
                var message = update.getMessage();
                        if(message!=null){
                            var command = message.getText();
                            commandContainer.retrieveCommand(command).execute(update);
                        }
            }
        }else {
            commandContainer.retrieveCommand(REGISTRATION.getCommandName()).execute(update);
        }
    }

    private void processDocMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account.isPresent()) {
            if (State.ANNEX_PENDING.equals(account.get().getState())) {
                commandContainer.retrieveCommand(ANNEX_PENDING.getCommandName()).execute(update);
            }else if(State.DOWNLOAD_MESSAGE_PENDING.equals(account.get().getState())){
                commandContainer.retrieveCommand(DOWNLOAD_MESSAGE_PENDING.getCommandName()).execute(update);
            }else{
                commandContainer.retrieveCommand("unknownCommand").execute(update);
            }
        }else{
            commandContainer.retrieveCommand(REGISTRATION.getCommandName()).execute(update);
        }
    }

    private void processPhotoMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account.isPresent()) {
            if (State.ANNEX_PENDING.equals(account.get().getState())) {
                commandContainer.retrieveCommand(ANNEX_PENDING.getCommandName()).execute(update);
            }else{
                commandContainer.retrieveCommand("unknownCommand").execute(update);
            }
        }else{
            commandContainer.retrieveCommand(REGISTRATION.getCommandName()).execute(update);
        }
    }

    private void processUnsupportedMessageTypeView(Update update) {
        commandContainer.retrieveCommand("unknownCommand").execute(update);
    }
}
