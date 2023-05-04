package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the title in the model and sets the state  {@link Command,State}.
 */
public class AfterChangeTitleCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepository;

    public AfterChangeTitleCommand(ModifyDataBaseService modifyDataBaseService,
                                   SendBotMessageService sendBotMessageService,
                                   MessageRepositoryService messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {

        var newTitle = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var textForOld = prepareSuccessTextForChangingLastMessage();

        var message = messageRepository.getMessage(chatId);
        message.setTitle(newTitle);
        messageRepository.putMessage(chatId,message);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var text = prepareTextForMessage(chatId);
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long id) {
        var message = messageRepository.getMessage(id);

        return prepareTextForPreviewMessage(message);
    }

}
