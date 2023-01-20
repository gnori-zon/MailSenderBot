package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.command.CommandContainer;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.CommandName.KEY_FOR_MAIL_PENDING;
import static org.gnori.mailsenderbot.command.CommandName.MAIL_PENDING;

@Service
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {

    private final CommandContainer commandContainer;
    private final ModifyDataBaseService modifyDataBaseService;

    public MessageTypesDistributorServiceImpl(SendBotMessageService sendBotMessageService,
                                              ModifyDataBaseService modifyDataBaseService,
                                              MessageRepository messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.commandContainer = new CommandContainer(sendBotMessageService, modifyDataBaseService,messageRepository, this);
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
        //TODO implements invoke RegistrationCommand if account not found in db
        var account = modifyDataBaseService.findAccountById(update.getMessage().getChatId());
        if(account!=null){
            if(State.KEY_FOR_MAIL_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(KEY_FOR_MAIL_PENDING.getCommandName()).execute(update);
            }else if(State.MAIL_PENDING.equals(account.getState())){
                commandContainer.retrieveCommand(MAIL_PENDING.getCommandName()).execute(update);
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

    }

    private void processPhotoMessage(Update update) {

    }

    private void processUnsupportedMessageTypeView(Update update) {

    }
}
