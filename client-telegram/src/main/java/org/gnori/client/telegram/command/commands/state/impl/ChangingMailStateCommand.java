package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.bot.SendBotMessageService;
import org.gnori.client.telegram.service.AccountUpdateFailure;
import org.gnori.client.telegram.service.bot.model.CallbackButtonData;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
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

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Component
@RequiredArgsConstructor
public class ChangingMailStateCommand implements StateCommand {

    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;
    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        var chatId = account.getChatId();

        final String textForOldMessage = updateAccount(account, update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> switch (failure) {
                            case ALREADY_EXIST_MAIL -> prepareTextForAfterMailIsExistChangeMailMessage();
                            default -> prepareTextForAfterInvalidMailChangeMailMessage();
                        }
                );

        final int lastMessageId = update.getMessage().getMessageId() - 1;

        account.setState(State.DEFAULT);
        accountService.saveAccount(account);

        sendBotMessageService.editMessage(chatId, lastMessageId, textForOldMessage, Collections.emptyList(), false);

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(successUpdateMailCallbackButtonPreparerParamOf());

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MAIL;
    }

    private CallbackButtonDataPreparerParam successUpdateMailCallbackButtonPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_PROFILE_INFO_ITEM,
                MenuStepCommandType.SETUP_SETTINGS_ITEM,
                true,
                false
        );
    }

    private Result<String, AccountUpdateFailure> extractText(Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(String::trim)
                .map(Result::<String, AccountUpdateFailure>success)
                .orElseGet(() -> Result.failure(AccountUpdateFailure.NOT_VALID_MAIL));
    }

    private Result<Empty, AccountUpdateFailure> updateAccount(Account account, Update update) {

        return extractText(update)
                .flatMapSuccess(mailRaw ->
                        UtilsCommand.validateMail(mailRaw)
                                .mapFailure(failure -> AccountUpdateFailure.NOT_VALID_MAIL)
                )
                .doIfSuccess(validMail ->
                        accountService.updateMailById(account.getId(), validMail)
                                .doIfSuccess(empty -> account.setEmail(validMail))
                )
                .mapSuccess(validMail -> Empty.INSTANCE);
    }
}
