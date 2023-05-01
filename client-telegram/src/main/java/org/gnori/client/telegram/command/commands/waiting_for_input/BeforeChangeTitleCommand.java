package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeTitleMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Displays a pending message and sets the state {@link Command,State}.
 */
public class BeforeChangeTitleCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;

    public BeforeChangeTitleCommand(SendBotMessageService sendBotMessageService, ModifyDataBaseService modifyDataBaseService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForBeforeChangeTitleMessage();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        modifyDataBaseService.updateStateById(chatId, State.TITLE_PENDING);
        sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),true);
    }
}
