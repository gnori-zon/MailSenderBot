package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareTextForProfileMessage;

public class AfterChangeKeyForMailCommand implements Command {

    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final CryptoTool cryptoTool;


    public AfterChangeKeyForMailCommand(ModifyDataBaseService modifyDataBaseService,
                                        SendBotMessageService sendBotMessageService,
                                        CryptoTool cryptoTool) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public void execute(Update update) {
        var newKey = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var textForOld = "✔Успешно";
        if(newKey!=null) {
            newKey = cryptoTool.encrypt(newKey);
            modifyDataBaseService.updateKeyForMailById(chatId, newKey);
            modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        }else{
            textForOld = "❌Необходимо ввести ключ, попробуйте снова";
        }

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(account);
        var newCallbackData = prepareCallbackDataForProfileMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }

}
