package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.utils.preparers.TextPreparer;
import org.gnori.data.dto.AccountDto;
import org.gnori.shared.crypto.CryptoTool;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterEmptyKeyChangeKeyForMailMessage;

@Component
@RequiredArgsConstructor
public class ChangingKeyStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;
    private final CryptoTool cryptoTool;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();

        final String textForOld = Optional.ofNullable(update.getMessage().getText())
                .map(cryptoTool::encrypt)
                .map(encryptResult -> encryptResult
                        .doIfSuccess(encryptedKey -> accountService.updateKeyForMailById(chatId, encryptedKey))
                        .fold(
                                success -> prepareSuccessTextForChangingLastMessage(),
                                failure -> prepareTextForAfterEmptyKeyChangeKeyForMailMessage()
                        )
                )
                .orElseGet(TextPreparer::prepareTextForAfterEmptyKeyChangeKeyForMailMessage);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<List<String>> newCallbackData = prepareCallbackDataForProfileMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_KEY;
    }
}
