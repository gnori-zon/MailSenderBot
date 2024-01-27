package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.message.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.message.MessageUpdateFailure;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.client.telegram.utils.command.UtilsCommandFailure;
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
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

        final String textForOld = updateMessage(chatId, update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareTextForAfterNotNumberChangeCountForRecipientsMessage()
                );

        accountService.updateStateById(chatId, State.DEFAULT);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final Message message = messageRepositoryService.getMessage(chatId);
        final String text = prepareTextForPreviewMessage(message);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_CONT_FOR_RECIPIENTS;
    }

    private Result<Empty, MessageUpdateFailure> updateMessage(long chatId, Update update) {

        return extractNewCount(update)
                        .doIfSuccess(newCount -> {

                            final Message message = messageRepositoryService.getMessage(chatId);
                            messageRepositoryService.putMessage(chatId, message.withCountForRecipient(newCount));
                        })
                        .mapSuccess(newCount -> Empty.INSTANCE)
                        .mapFailure(failure -> MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }

    private Result<Integer, UtilsCommandFailure> extractNewCount(Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(String::trim)
                .map(UtilsCommand::parseNumber)
                .orElseGet(() -> Result.failure(UtilsCommandFailure.NUMBER_FORMAT_EXCEPTION));
    }
}
