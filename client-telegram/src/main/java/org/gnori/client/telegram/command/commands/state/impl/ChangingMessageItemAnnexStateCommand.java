package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.data.model.Message;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemAnnexStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepositoryService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final Message message = messageRepositoryService.getMessage(chatId);
        final String textForOld = prepareSuccessTextForChangingLastMessage();

        if (update.getMessage().hasDocument()) {
            final Document newDocument = update.getMessage().getDocument();
            if (newDocument != null) {
//                message.setDocAnnex(newDocument); // todo: refactor on copy
            }

        } else if (update.getMessage().hasPhoto()) {
            final int countPhoto = update.getMessage().getPhoto().size();
            final int photoIndex = (countPhoto > 1)
                    ? countPhoto - 1 :
                    0;

            final PhotoSize photo = update.getMessage().getPhoto().get(photoIndex);
            if (photo != null) {
//                message.setPhotoAnnex(photo); // todo: refactor on copy
            }
        }

        messageRepositoryService.putMessage(chatId, message);
        accountService.updateStateById(chatId, State.DEFAULT);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForPreviewMessage(message);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_ANNEX;
    }
}