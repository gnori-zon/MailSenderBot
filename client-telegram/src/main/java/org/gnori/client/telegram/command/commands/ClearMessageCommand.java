package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.MessageRepositoryService;
import org.gnori.store.model.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Clears a mailing in the model {@link Command}.
 */
public class ClearMessageCommand implements Command {
    private final MessageRepositoryService messageRepository;
    private final SendBotMessageService sendBotMessageService;

    public ClearMessageCommand(SendBotMessageService sendBotMessageService, MessageRepositoryService messageRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var message = new Message();
        var text = prepareTextForPreviewMessage(message);
        var textForLatMessage = prepareSuccessTextForChangingLastMessage();
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        messageRepository.putMessage(chatId,message);
        sendBotMessageService.executeEditMessage(chatId,messageId,textForLatMessage, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }
}
