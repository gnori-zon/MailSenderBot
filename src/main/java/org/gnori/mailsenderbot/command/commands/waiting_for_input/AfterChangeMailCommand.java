package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.UtilsCommand.*;

public class AfterChangeMailCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;


    public AfterChangeMailCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var mail = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var textForOldMessage = "";

        var emailIsValid = validateMail(mail);
        if(emailIsValid) {
            try {
                modifyDataBaseService.updateMailById(chatId, mail);
                textForOldMessage = "✔Успешно";
            } catch (DataIntegrityViolationException e) {
                textForOldMessage = "❌Такой mail уже используется, попробуйте снова";
            }
            modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        }else {
            textForOldMessage = "❌Некорректный mail, попробуйте снова";
        }
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(account);
        var newCallbackData = prepareCallbackDataForProfileMessage();

        sendBotMessageService.executeEditMessage(chatId, lastMessageId, textForOldMessage, Collections.emptyList(), false);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData,true);

    }

}
