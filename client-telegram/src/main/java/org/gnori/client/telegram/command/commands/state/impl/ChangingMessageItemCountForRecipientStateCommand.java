package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.client.telegram.utils.preparers.TextPreparer;
import org.gnori.data.model.Message;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterNotNumberChangeCountForRecipientsMessage;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemCountForRecipientStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepositoryService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final Message message = messageRepositoryService.getMessage(chatId);

        final String textForOld = Optional.ofNullable(update.getMessage().getText())
                .map(String::trim)
                .map(UtilsCommand::parseNumber)
                .map(parseResult -> parseResult
                                .doIfSuccess(newCount -> {
//                                    message.setCountForRecipient(newCount);
                                    messageRepositoryService.putMessage(chatId, message);
                                })
                                .fold(
                                        success -> prepareSuccessTextForChangingLastMessage(),
                                        failure -> prepareTextForAfterNotNumberChangeCountForRecipientsMessage()
                                )
                )
                .orElseGet(TextPreparer::prepareTextForAfterNotNumberChangeCountForRecipientsMessage);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);


        final String text = prepareTextForPreviewMessage(message);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_CONT_FOR_RECIPIENTS;
    }
}
