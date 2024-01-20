package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.data.model.Message;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
        final String dateRaw = update.getMessage().getText();

        final Message message = messageRepositoryService.getMessage(chatId);

        final String textForOld = UtilsCommand.parseLocalDate(dateRaw)
                .mapSuccess(newSentDate -> {

                    if (LocalDate.now().isAfter(newSentDate)) {
                        return prepareTextInvalidDateForAfterChangeSentDateMessage();
                    }

//                    message.setSentDate(newSentDate); // todo: refactor on copy
                    messageRepositoryService.putMessage(chatId, message);

                    return prepareSuccessTextForChangingLastMessage();
                })
                .fold(
                        successMessage -> successMessage,
                        failure -> prepareTextInvalidDateForAfterChangeSentDateMessage()
                );

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForPreviewMessage(message);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_SENT_DATE;
    }
}
