package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.command.commands.Utils.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.command.commands.Utils.prepareTextForPreviewMessage;

public class AfterChangeTitleCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public AfterChangeTitleCommand(ModifyDataBaseService modifyDataBaseService,
                                   SendBotMessageService sendBotMessageService,
                                   MessageRepository messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {

        var newTitle = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        var message = messageRepository.getMessage(chatId);
        message.setTitle(newTitle);
        messageRepository.putMessage(chatId,message);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var text = prepareTextForMessage(chatId);
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,"✔Успешно", Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long id) {
        var message = messageRepository.getMessage(id);

        return prepareTextForPreviewMessage(message);
    }

}