package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeforeChangeMailMessage;
/**
 * Displays a pending message and sets the state {@link Command,State}.
 */
public class BeforeChangeMailCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;

    public BeforeChangeMailCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }
    @Override
    public void execute(Update update) {
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var text = prepareTextForBeforeChangeMailMessage();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            modifyDataBaseService.updateStateById(chatId, State.MAIL_PENDING);
            sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),true);
    }
}
