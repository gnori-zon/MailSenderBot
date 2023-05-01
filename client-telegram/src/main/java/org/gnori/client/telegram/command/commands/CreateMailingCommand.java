package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.MessageRepositoryService;
import org.gnori.store.model.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Creates a mailing in the model {@link Command}.
 */
public class CreateMailingCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepository;

    public CreateMailingCommand(SendBotMessageService sendBotMessageService, MessageRepositoryService messageRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForMessage(chatId);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long id) {
        var message = messageRepository.getMessage(id);
        if (message == null) {
            var newMessage = new Message();
            messageRepository.putMessage(id, newMessage);
            message = newMessage;
        }
        return prepareTextForPreviewMessage(message);
    }
}
