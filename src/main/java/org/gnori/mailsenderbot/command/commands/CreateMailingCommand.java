package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForPreviewMessage;
/**
 * Creates a mailing in the model {@link Command}.
 */
public class CreateMailingCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public CreateMailingCommand(SendBotMessageService sendBotMessageService, MessageRepository messageRepository) {
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
