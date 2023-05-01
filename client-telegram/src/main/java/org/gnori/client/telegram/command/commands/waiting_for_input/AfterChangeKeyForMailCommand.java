package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterEmptyKeyChangeKeyForMailMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.AccountDto;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the key for mail in the Database and sets the state {@link Command,State}.
 */
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
        var textForOld = prepareSuccessTextForChangingLastMessage();
        if(newKey!=null) {
            newKey = cryptoTool.encrypt(newKey);
            modifyDataBaseService.updateKeyForMailById(chatId, newKey);
        }else{
            textForOld = prepareTextForAfterEmptyKeyChangeKeyForMailMessage();
        }

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(new AccountDto(account.orElse(new Account())));
        var newCallbackData = prepareCallbackDataForProfileMessage();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }

}
