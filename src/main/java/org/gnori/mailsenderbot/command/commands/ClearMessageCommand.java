package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForPreviewMessage;
/**
 * Clears a mailing in the model {@link Command}.
 */
public class ClearMessageCommand implements Command {
    private final MessageRepository messageRepository;
    private final SendBotMessageService sendBotMessageService;

    public ClearMessageCommand(SendBotMessageService sendBotMessageService, MessageRepository messageRepository) {
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
