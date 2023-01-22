package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.commands.Utils.prepareCallbackDataForBeginningMessage;

public class SendCurrentMailCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepository messageRepository;

    public SendCurrentMailCommand(SendBotMessageService sendBotMessageService,
                                  ModifyDataBaseService modifyDataBaseService,
                                  MessageRepository messageRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        //TODO added impl service for send Mail
        // and send Success alert for telegram
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var text = "отправлено:✔" +
                "\nВыберите необходимый пункт👇🏿";
        var messageToSend = messageRepository.getMessage(chatId);
        //TODO send mail logic

        createAndAddMessageSentRecord(chatId, messageToSend);
        messageRepository.removeMessage(chatId);
        sendBotMessageService.executeEditMessage(chatId, messageId, text, newCallbackData, false);

    }

    private void createAndAddMessageSentRecord(Long id, Message message) {
        var countMessages = message.getRecipients().size() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();
        modifyDataBaseService.addMessageSentRecord(id, messageSentRecord);
    }

}
