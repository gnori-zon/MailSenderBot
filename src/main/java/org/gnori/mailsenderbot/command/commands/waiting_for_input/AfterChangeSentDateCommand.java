package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.*;
import static org.gnori.mailsenderbot.utils.UtilsCommand.getSimpleDateFormat;

public class AfterChangeSentDateCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public AfterChangeSentDateCommand(ModifyDataBaseService modifyDataBaseService,
                                      SendBotMessageService sendBotMessageService,
                                      MessageRepository messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        var dateRaw = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var textForOld = prepareSuccessTextForChangingLastMessage();
        var message = messageRepository.getMessage(chatId);

        var dateFormat = getSimpleDateFormat();
        try {
            var newSentDate = dateFormat.parse(dateRaw);
            if(new Date().compareTo(newSentDate) < 0){
                message.setSentDate(newSentDate);
                messageRepository.putMessage(chatId,message);
            }else{
                textForOld = prepareTextInvalidDateForAfterChangeSentDateMessage();
            }
        } catch (ParseException e) {
            textForOld = prepareTextInvalidDateForAfterChangeSentDateMessage();
        }

        var text = prepareTextForPreviewMessage(message);
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();
        var lastMessageId = update.getMessage().getMessageId() - 1;

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }
}
