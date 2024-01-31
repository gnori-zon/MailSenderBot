package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.AccountUpdateFailure;
import org.gnori.client.telegram.service.impl.bot.model.CallbackButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
import org.gnori.shared.crypto.CryptoTool;
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

import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Component
@RequiredArgsConstructor
public class ChangingKeyStateCommand implements StateCommand {

    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;
    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;
    private final CryptoTool cryptoTool;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();

        final String textForOld = updateAccount(account, update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareTextForAfterEmptyKeyChangeKeyForMailMessage()
                );

        final int lastMessageId = update.getMessage().getMessageId() - 1;

        account.setState(State.DEFAULT);
        accountService.saveAccount(account);

        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(successUpdateKeyCallbackButtonPreparerParamOf());

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_KEY;
    }

    private CallbackButtonDataPreparerParam successUpdateKeyCallbackButtonPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_PROFILE_INFO_ITEM,
                MenuStepCommandType.SETUP_SETTINGS_ITEM,
                true,
                false
        );
    }

    private Result<Empty, AccountUpdateFailure> updateAccount(Account account, Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(cryptoTool::encrypt)
                .map(encryptResult -> encryptResult
                        .doIfSuccess(encryptedKey -> {

                            account.setKeyForMail(encryptedKey);
                            accountService.saveAccount(account);
                        })
                        .mapSuccess(encryptedKey -> Empty.INSTANCE)
                        .mapFailure(failure -> AccountUpdateFailure.BAD_ENCRYPT)
                )
                .orElseGet(() -> Result.failure(AccountUpdateFailure.BAD_ENCRYPT));
    }
}
