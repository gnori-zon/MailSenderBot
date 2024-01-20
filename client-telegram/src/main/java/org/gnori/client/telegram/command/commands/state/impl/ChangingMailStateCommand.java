package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.command.UtilsCommand.validateMail;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterInvalidMailChangeMailMessage;

@Component
@RequiredArgsConstructor
public class ChangingMailStateCommand implements StateCommand {

    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        var chatId = account.getChatId();

        final String textForOldMessage = Optional.ofNullable(update.getMessage().getText())
                .map(String::trim)
                .map(UtilsCommand::validateMail)
                .map(validateResult -> validateResult
                        .mapFailure(failure -> prepareTextForAfterInvalidMailChangeMailMessage())
                        .mapSuccess(validMail -> accountService.updateMailById(chatId, validMail)
                                .mapFailure(failure -> prepareTextForAfterMailIsExistChangeMailMessage())
                        )
                        .fold(empty -> prepareSuccessTextForChangingLastMessage(), failureMessage -> failureMessage)
                )
                .orElse(prepareTextForAfterInvalidMailChangeMailMessage());

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOldMessage, Collections.emptyList(), false);

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<List<String>> newCallbackData = prepareCallbackDataForProfileMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MAIL;
    }
}
