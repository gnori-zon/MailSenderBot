package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.command.CommandContainer;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {

    private final CommandContainer commandContainer;

    public MessageTypesDistributorServiceImpl(SendBotMessageService sendBotMessageService,
                                              ModifyDataBaseService modifyDataBaseService) {
        this.commandContainer = new CommandContainer(sendBotMessageService, modifyDataBaseService);
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

        var command = update.getMessage().getText();
        commandContainer.retrieveCommand(command).execute(update);
    }

    private void processDocMessage(Update update) {

    }

    private void processPhotoMessage(Update update) {

    }

    private void processUnsupportedMessageTypeView(Update update) {

    }
}
