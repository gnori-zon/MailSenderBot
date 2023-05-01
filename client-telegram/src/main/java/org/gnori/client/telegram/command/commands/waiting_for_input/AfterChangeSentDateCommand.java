package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.UtilsCommand.getSimpleDateFormat;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextInvalidDateForAfterChangeSentDateMessage;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the sent date in the model and sets the state  {@link Command,State}.
 */
public class AfterChangeSentDateCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepository;

    public AfterChangeSentDateCommand(ModifyDataBaseService modifyDataBaseService,
                                      SendBotMessageService sendBotMessageService,
                                      MessageRepositoryService messageRepository) {
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
