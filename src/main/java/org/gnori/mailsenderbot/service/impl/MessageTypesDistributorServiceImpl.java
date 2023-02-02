package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.command.CommandContainer;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.*;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.CommandName.*;

@Log4j
@Service
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {

    private final CommandContainer commandContainer;
    private final ModifyDataBaseService modifyDataBaseService;
    public MessageTypesDistributorServiceImpl(SendBotMessageService sendBotMessageService,
                                              ModifyDataBaseService modifyDataBaseService,
                                              MessageRepository messageRepository,
                                              MailSenderService mailSenderService,
                                              FileService fileService,
                                              CryptoTool cryptoTool) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.commandContainer = new CommandContainer(sendBotMessageService,
                modifyDataBaseService,
                messageRepository,
                mailSenderService,
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
        var command = update.getCallbackQuery().getData();
        commandContainer.retrieveCommand(command).execute(update);
    }

    private void processTextMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account!=null){
            if(State.KEY_FOR_MAIL_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(KEY_FOR_MAIL_PENDING.getCommandName()).execute(update);
            }else if(State.MAIL_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(MAIL_PENDING.getCommandName()).execute(update);
            }else if(State.TITLE_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(TITLE_PENDING.getCommandName()).execute(update);
            }else if(State.CONTENT_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(CONTENT_PENDING.getCommandName()).execute(update);
            }else if(State.RECIPIENTS_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(RECIPIENTS_PENDING.getCommandName()).execute(update);
            }else if(State.COUNT_FOR_RECIPIENT_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(COUNT_FOR_RECIPIENT_PENDING.getCommandName()).execute(update);
            }else{
                var message = update.getMessage();
                        if(message!=null){
                            var command = message.getText();
                            commandContainer.retrieveCommand(command).execute(update);
                        }
            }
        }else {
            var command = update.getMessage().getText();
            commandContainer.retrieveCommand(command).execute(update);
        }
    }

    private void processDocMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account!=null) {
            if (State.ANNEX_PENDING.equals(account.getState())) {
                commandContainer.retrieveCommand(ANNEX_PENDING.getCommandName()).execute(update);
            }else if(State.DOWNLOAD_MESSAGE_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(DOWNLOAD_MESSAGE_PENDING.getCommandName()).execute(update);
            }
        }
    }

    private void processPhotoMessage(Update update) {
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account!=null) {
            if (State.ANNEX_PENDING.equals(account.getState())) {
                commandContainer.retrieveCommand(ANNEX_PENDING.getCommandName()).execute(update);
            }
        }
    }

    private void processUnsupportedMessageTypeView(Update update) {
        commandContainer.retrieveCommand("unknownCommand").execute(update);
    }
}
