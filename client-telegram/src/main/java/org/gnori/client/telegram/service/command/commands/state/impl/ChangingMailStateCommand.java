package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.account.updater.AccountUpdater;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.BotMessageSender;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.message.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChangingMailStateCommand implements StateCommand {

    private final AccountUpdater accountUpdater;
    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountUpdater.updateState(account.getId(), State.DEFAULT);

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String mailRaw = update.getMessage().getText();
        final String textForOldMessage = accountUpdater.updateMail(account.getId(), mailRaw)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> switch (failure) {
                            case ALREADY_EXIST_MAIL -> prepareTextForAfterMailIsExistChangeMailMessage();
                            default -> prepareTextForAfterInvalidMailChangeMailMessage();
                        }
                );

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOldMessage));

        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(successUpdateMailCallbackButtonPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
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
}
