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
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemTitleStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;
    private final MessageRepositoryService messageRepositoryService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final String newTitle = update.getMessage().getText();
        final String textForOld = prepareSuccessTextForChangingLastMessage();

        final Message message = messageRepositoryService.getMessage(chatId);
//        message.setTitle(newTitle); // todo: refactor on copy
        messageRepositoryService.putMessage(chatId, message);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForPreviewMessage(message);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_TITLE;
    }
}
