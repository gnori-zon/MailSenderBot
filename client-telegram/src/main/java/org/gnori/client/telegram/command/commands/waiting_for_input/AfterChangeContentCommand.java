package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the content in the model and sets the state {@link Command,State}.
 */
public class AfterChangeContentCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepository;

    public AfterChangeContentCommand(ModifyDataBaseService modifyDataBaseService,
                                   SendBotMessageService sendBotMessageService,
                                   MessageRepositoryService messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {

        var newContent = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var textForOld = prepareSuccessTextForChangingLastMessage();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        var message = messageRepository.getMessage(chatId);
        message.setText(newContent);
        messageRepository.putMessage(chatId,message);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var text = prepareTextForPreviewMessage(message);
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }
}
