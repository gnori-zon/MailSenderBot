package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.account.updater.AccountUpdater;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.command.utils.preparers.text.TextPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.PatternTextPreparerParam;
import org.gnori.data.dto.AccountDto;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileInfoCallbackCommand implements CallbackCommand {

    private final TextPreparer textPreparer;
    private final AccountUpdater accountService;
    private final BotMessageEditor botMessageEditor;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountService.updateState(account.getId(), State.DEFAULT);

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final AccountDto accountDto = new AccountDto(account);
        final String text = textPreparer.prepare(PatternTextPreparerParam.profileInfo(accountDto));
        final List<ButtonData> callbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerOf());

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, callbackButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.PROFILE_INFO;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_PROFILE_INFO_ITEM,
                MenuStepCommandType.PROFILE_INFO,
                true,
                false
        );
    }
}
