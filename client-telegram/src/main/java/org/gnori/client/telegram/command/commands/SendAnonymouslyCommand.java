package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.store.dao.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.StateMessage;
import org.gnori.store.model.SendMode;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Sends a mailing with the bot's mail {@link Command}.
 */
public class SendAnonymouslyCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepositoryService messageRepository;
    private final QueueManager queueManager;

    public SendAnonymouslyCommand(SendBotMessageService sendBotMessageService,
                                  ModifyDataBaseService modifyDataBaseService,
                                  MessageRepositoryService messageRepository,
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
        messageToSend.setSendMode(SendMode.ANONYMOUSLY);
        var text = prepareTextForSendCurrentAndAnonymouslyMessage();

        queueManager.addInQueue(messageToSend);
        messageRepository.removeMessage(chatId);
        modifyDataBaseService.updateStateMessageById(chatId, StateMessage.QUEUE);

        sendBotMessageService.executeEditMessage(chatId, messageId, text, newCallbackData, false);
    }
}
