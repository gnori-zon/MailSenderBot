package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.message.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.message.MessageUpdateFailure;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemSentDateStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepositoryService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();

        final String textForOld = updateMessage(chatId, update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareTextInvalidDateForAfterChangeSentDateMessage()
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
        return StateCommandType.CHANGING_MESSAGE_ITEM_SENT_DATE;
    }

    private Result<Empty, MessageUpdateFailure> updateMessage(long chatId, Update update) {

        return extractSentDate(update)
                .flatMapSuccess(this::validateSentDate)
                .doIfSuccess(newSentDate -> {

                    final Message message = messageRepositoryService.getMessage(chatId);
                    messageRepositoryService.putMessage(chatId, message.withSentDate(newSentDate));
                })
                .mapSuccess(newSentDate -> Empty.INSTANCE);
    }

    private Result<LocalDate, MessageUpdateFailure> validateSentDate(LocalDate sendDate) {

        if (LocalDate.now().isAfter(sendDate)) {
            return Result.success(sendDate);
        }

        return Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }

    private Result<LocalDate, MessageUpdateFailure> extractSentDate(Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(UtilsCommand::parseLocalDate)
                .map(resultParsed -> resultParsed.mapFailure(failure -> MessageUpdateFailure.INCORRECT_INPUT_TYPE))
                .orElseGet(() -> Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE));
    }
}
