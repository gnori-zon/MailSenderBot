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
import org.gnori.client.telegram.service.command.utils.command.UtilsCommand;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
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
public class ChangingMailStateCommand implements StateCommand {

    private final AccountService accountService;
    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountService.updateStateById(account.getId(), State.DEFAULT);

        var chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOldMessage = updateAccount(account, update)
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
