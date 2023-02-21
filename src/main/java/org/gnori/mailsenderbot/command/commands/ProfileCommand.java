package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForProfileMessage;
/**
 * Displays user data {@link Command}.
 */
public class ProfileCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;

    public ProfileCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var account = modifyDataBaseService.findAccountDTOById(chatId);
        var text = prepareTextForProfileMessage(account);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForProfileMessage();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}
