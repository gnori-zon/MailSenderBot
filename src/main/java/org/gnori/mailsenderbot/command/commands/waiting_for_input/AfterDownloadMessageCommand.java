package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.FileDataParser.*;
import static org.gnori.mailsenderbot.utils.TextPreparer.*;
import static org.gnori.mailsenderbot.utils.UtilsCommand.getSimpleDateFormat;

@Log4j
public class AfterDownloadMessageCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepository messageRepository;
    private final FileService fileService;

    public AfterDownloadMessageCommand(SendBotMessageService sendBotMessageService, ModifyDataBaseService modifyDataBaseService, MessageRepository messageRepository, FileService fileService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
        this.messageRepository = messageRepository;
        this.fileService = fileService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();
        var message = messageRepository.getMessage(chatId);
        var textForOld = prepareTextForAfterBadDownloadMessage();

        if (update.getMessage().hasDocument()) {
            var newDocument = update.getMessage().getDocument();
            var content = getContent(chatId, newDocument);
            var titleForMessage = getTitleFromContent(content);
            var textForMessage = getTextFromContent(content);
            var recipients = getRecipientsFromContent(content);
            var countForRecipient = getCountForRecipientFromContent(content);
            var sentDateRaw = getSentDateFromContent(content);
            message.setCountForRecipient(countForRecipient);
            message.setRecipients(recipients);
            message.setTitle(titleForMessage);
            message.setText(textForMessage);
            if (sentDateRaw != null) {
                try {
                    var dateFormat = getSimpleDateFormat();
                    var newSentDate = dateFormat.parse(sentDateRaw);
                    if (new Date().compareTo(newSentDate) < 0) {
                        message.setSentDate(newSentDate);
                    }
                } catch (ParseException ignored) {}
            }
                messageRepository.putMessage(chatId, message);
                textForOld = prepareTextForAfterSuccessDownloadMessage();
        }
        var text = prepareTextForPreviewMessage(message);

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }

    private String getContent(Long id, Document doc) {
        StringBuilder content = new StringBuilder();
            var document = fileService.processMail(id, doc);
                try {
                    content.append(Files.readString(document.getFile().toPath(), StandardCharsets.UTF_8));
            } catch (IOException | NullPointerException e) {
                log.error(e);
            }

            return content.toString();
    }

}
