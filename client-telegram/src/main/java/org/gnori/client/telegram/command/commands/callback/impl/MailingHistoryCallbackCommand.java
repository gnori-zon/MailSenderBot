package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.store.domain.service.mailing.MailingHistoryService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForMessage;

@Component
@RequiredArgsConstructor
public class MailingHistoryCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MailingHistoryService mailingHistoryService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();

        final MailingHistory mailingHistory = mailingHistoryService.getMailingHistoryById(chatId)
                .orElse(new MailingHistory());
        final String text = prepareTextForMessage(new MailingHistoryDto(mailingHistory));

        sendBotMessageService.editMessage(chatId, messageId, text, Collections.emptyList(), true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.MAILING_HISTORY;
    }
}
