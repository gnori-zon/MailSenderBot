package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gnori.mailsenderbot.utils.UtilsCommand.*;

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

        if (update.getMessage().hasDocument()) {
            var newDocument = update.getMessage().getDocument();
            var content = getContent(chatId,newDocument);
            var titleForMessage = getTitleFromContent(content);
            var textForMessage =  getTextFromContent(content);
            var recipients = getRecipientsFromContent(content);
            var countForRecipient = getCountForRecipientFromContent(content);
            message.setCountForRecipient(countForRecipient);
            message.setRecipients(recipients);
            message.setTitle(titleForMessage);
            message.setText(textForMessage);
            messageRepository.putMessage(chatId,message);

        }
        var text = prepareTextForPreviewMessage(message);

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        sendBotMessageService.executeEditMessage(chatId,lastMessageId,"✔Успешно", Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }

    private String getContent(Long id, Document doc) {
        StringBuilder content = new StringBuilder();
            var document = fileService.processMail(id, doc);

//            try (var reader = new FileReader(document.getFile())) {
//                var buffer = new char[256];
//                int constraint;
//                while ((constraint = reader.read(buffer)) > 0) {
//
//                    if (constraint < 256) {
//                        buffer = Arrays.copyOf(buffer, constraint);
//                    }
//                    content.append(buffer.toString());
//                }
                try {
                    content.append(Files.readString(document.getFile().toPath(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                //TODO error
            }
            return content.toString();
    }

}
