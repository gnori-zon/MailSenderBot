package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.AccountUpdateFailure;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.BotMessageSender;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
import org.gnori.shared.crypto.CryptoTool;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChangingKeyStateCommand implements StateCommand {

    private final CryptoTool cryptoTool;
    private final AccountService accountService;
    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountService.updateStateById(account.getId(), State.DEFAULT);

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOld = updateAccount(account, update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareTextForAfterEmptyKeyChangeKeyForMailMessage()
                );

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOld));

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(successUpdateKeyCallbackButtonPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
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
