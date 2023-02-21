package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.StateMessage;
import org.gnori.mailsenderbot.model.SendMode;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;
/**
 * Sends a mailing with the user's mail {@link Command}.
 */
public class SendCurrentMailCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepository messageRepository;
    private final QueueManager queueManager;

    public SendCurrentMailCommand(SendBotMessageService sendBotMessageService,
                                  ModifyDataBaseService modifyDataBaseService,
                                  MessageRepository messageRepository,
                                  QueueManager queueManager) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
        this.messageRepository = messageRepository;
        this.queueManager = queueManager;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var messageToSend = messageRepository.getMessage(chatId);
        var textForWaiting = prepareTextForWaitingForConcreteSendingMessage();

        sendBotMessageService.executeEditMessage(chatId, messageId, textForWaiting, Collections.emptyList(), false);
        messageToSend.setChatId(chatId);
        messageToSend.setSendMode(SendMode.CURRENT_MAIL);
        var text = prepareTextForSendCurrentAndAnonymouslyMessage();

        queueManager.addInQueue(messageToSend);
        messageRepository.removeMessage(chatId);
        modifyDataBaseService.updateStateMessageById(chatId, StateMessage.QUEUE);

        sendBotMessageService.executeEditMessage(chatId, messageId, text, newCallbackData, false);
    }
}
