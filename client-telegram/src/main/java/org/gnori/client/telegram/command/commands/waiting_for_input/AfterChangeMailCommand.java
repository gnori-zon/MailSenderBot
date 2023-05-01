package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.UtilsCommand.validateMail;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterInvalidMailChangeMailMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterMailIsExistChangeMailMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the mail in the Database and sets the state {@link Command,State}.
 */
public class AfterChangeMailCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;


    public AfterChangeMailCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var mail = update.getMessage().getText().trim();
        var chatId = update.getMessage().getChatId();
        var textForOldMessage = "";

        var emailIsValid = validateMail(mail);
        if(emailIsValid) {
            try {
                modifyDataBaseService.updateMailById(chatId, mail);
                textForOldMessage = prepareSuccessTextForChangingLastMessage();
            } catch (DataIntegrityViolationException e) {
                textForOldMessage = prepareTextForAfterMailIsExistChangeMailMessage();
            }
        }else {
            textForOldMessage = prepareTextForAfterInvalidMailChangeMailMessage();
        }
        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(new AccountDto(account.orElse(new Account())));
        var newCallbackData = prepareCallbackDataForProfileMessage();

        sendBotMessageService.executeEditMessage(chatId, lastMessageId, textForOldMessage, Collections.emptyList(), false);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData,true);

    }

}
