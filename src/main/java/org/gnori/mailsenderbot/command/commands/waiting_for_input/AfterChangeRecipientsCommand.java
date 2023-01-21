package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.gnori.mailsenderbot.command.commands.Utils.prepareTextForPreviewMessage;

public class AfterChangeRecipientsCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public AfterChangeRecipientsCommand(ModifyDataBaseService modifyDataBaseService,
                                     SendBotMessageService sendBotMessageService,
                                     MessageRepository messageRepository) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {

        var newRecipientsRaw = update.getMessage().getText().split(",");
        var chatId = update.getMessage().getChatId();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        var message = messageRepository.getMessage(chatId);
        var newRecipients = Arrays.stream(newRecipientsRaw).map(String::trim).collect(Collectors.toList());
        message.setRecipients(newRecipients);
        messageRepository.putMessage(chatId,message);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var text = prepareTextForMessage(chatId);
        List<String> callbackData = List.of("CHANGE_ITEM","SEND");
        List<String> callbackDataText = List.of("Изменить пункт","Отправить");
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,"✔Успешно", Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long id) {
        var message = messageRepository.getMessage(id);

        return prepareTextForPreviewMessage(message);
    }
}
