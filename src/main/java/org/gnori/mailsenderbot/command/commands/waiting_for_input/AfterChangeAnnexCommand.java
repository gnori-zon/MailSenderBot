package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForPreviewMessage;
/**
 * Validates and sets the annex in the model and sets the state {@link Command,State}.
 */
public class AfterChangeAnnexCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public AfterChangeAnnexCommand(ModifyDataBaseService modifyDataBaseService,
                                     SendBotMessageService sendBotMessageService,
                                     MessageRepository messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var message = messageRepository.getMessage(chatId);
        var textForOld = prepareSuccessTextForChangingLastMessage();

        if(update.getMessage().hasDocument()){
            var newDocument = update.getMessage().getDocument();
            if(newDocument!=null){
                message.setDocAnnex(newDocument);
            }
        }else if(update.getMessage().hasPhoto()){
            var photoSizeCount = update.getMessage().getPhoto().size();
            var photoIndex = (photoSizeCount > 1) ? photoSizeCount-1 : 0 ;
            var photo = update.getMessage().getPhoto().get(photoIndex);
            if (photo != null) {
                message.setPhotoAnnex(photo);
            }
        }

        messageRepository.putMessage(chatId,message);
        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var text = prepareTextForMessage(chatId);
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long id) {
        var message = messageRepository.getMessage(id);

        return prepareTextForPreviewMessage(message);
    }
}
