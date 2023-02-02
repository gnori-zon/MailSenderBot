package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.mail.internet.AddressException;
import java.util.Collections;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.*;

public class SendAnonymouslyCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepository messageRepository;
    private final MailSenderService mailSenderService;

    public SendAnonymouslyCommand(SendBotMessageService sendBotMessageService,
                                  ModifyDataBaseService modifyDataBaseService,
                                  MessageRepository messageRepository,
                                  MailSenderService mailSenderService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
        this.messageRepository = messageRepository;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var messageToSend = messageRepository.getMessage(chatId);
        var textForWaiting = prepareTextForWaitingForConcreteSendingMessage();


        sendBotMessageService.executeEditMessage(chatId, messageId, textForWaiting, Collections.emptyList(), false);
        var text = prepareTextForBadConcreteSendingMessage();
        try {
            var sendResult = mailSenderService.sendAnonymously(chatId, messageToSend);
            if (sendResult == 1) {
                text = prepareTextForSuccessConcreteSendingMessage();
                createAndAddMessageSentRecord(chatId, messageToSend);
                messageRepository.removeMessage(chatId);
            }
        } catch (AddressException e) {
            text += e.getMessage();
        } finally {
            text += "\n"+prepareTextForBeginningMessage();

            sendBotMessageService.executeEditMessage(chatId, messageId, text, newCallbackData, false);
        }
    }

    private void createAndAddMessageSentRecord(Long id, Message message){
        var countMessages = message.getRecipients().size() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();
        modifyDataBaseService.addMessageSentRecord(id, messageSentRecord);
    };
}
