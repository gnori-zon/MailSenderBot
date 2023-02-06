package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForBeforeChangeRecipientsMessage;
/**
 * Displays a pending message and sets the state {@link Command,State}.
 */
public class BeforeChangeRecipientsCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;

    public BeforeChangeRecipientsCommand(SendBotMessageService sendBotMessageService, ModifyDataBaseService modifyDataBaseService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForBeforeChangeRecipientsMessage();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        modifyDataBaseService.updateStateById(chatId, State.RECIPIENTS_PENDING);
        sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),true);
    }
}
