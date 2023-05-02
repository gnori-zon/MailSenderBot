package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.FileDataParser.getCountForRecipientFromContent;
import static org.gnori.client.telegram.utils.FileDataParser.getRecipientsFromContent;
import static org.gnori.client.telegram.utils.FileDataParser.getSentDateFromContent;
import static org.gnori.client.telegram.utils.FileDataParser.getTextFromContent;
import static org.gnori.client.telegram.utils.FileDataParser.getTitleFromContent;
import static org.gnori.client.telegram.utils.UtilsCommand.getSimpleDateFormat;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterBadDownloadMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterSuccessDownloadMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.FileService;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Validates and sets the params in the model and sets the state  {@link Command, State}.
 */
@Log4j2
public class AfterDownloadMessageCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageRepositoryService messageRepository;
    private final FileService fileService;

    public AfterDownloadMessageCommand(SendBotMessageService sendBotMessageService,
        ModifyDataBaseService modifyDataBaseService, MessageRepositoryService messageRepository, FileService fileService) {
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
                    var isDeleted = document.getFile().delete();
                    if(!isDeleted){
                        log.error("File:"+document.getFilename()+" not removed");
                    }
            } catch (IOException | NullPointerException e) {
                log.error(e);
            }

            return content.toString();
    }

}
