package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.CommandName.PROFILE;

public class AfterChangeKeyForMailCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageTypesDistributorService messageTypesDistributorService;


    public AfterChangeKeyForMailCommand(ModifyDataBaseService modifyDataBaseService,
                                        SendBotMessageService sendBotMessageService,
                                        MessageTypesDistributorService messageTypesDistributorService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageTypesDistributorService = messageTypesDistributorService;
    }

    @Override
    public void execute(Update update) {

        var newKey = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        modifyDataBaseService.updateKeyForMailById(chatId,newKey);

        var messageId = update.getMessage().getMessageId();
        // TODO поиск и удаление последнего сообщения бота + создание нового
        update.getMessage().setMessageId(messageId-1);
        update.setCallbackQuery(newCallbackQuery(update));
        messageTypesDistributorService.distributeMessageByType(update);
    }

    private CallbackQuery newCallbackQuery(Update update) {
        var callbackQuery = new CallbackQuery();
        callbackQuery.setData(PROFILE.getCommandName());
        callbackQuery.setMessage(update.getMessage());
        return callbackQuery;
    }
}
