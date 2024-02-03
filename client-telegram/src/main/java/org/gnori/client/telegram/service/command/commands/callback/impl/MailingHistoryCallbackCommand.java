package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.store.domain.service.mailing.MailingHistoryService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForMessage;

@Component
@RequiredArgsConstructor
public class MailingHistoryCallbackCommand implements CallbackCommand {

    private final BotMessageEditor botMessageEditor;
    private final MailingHistoryService mailingHistoryService;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();

        final MailingHistory mailingHistory = mailingHistoryService.getMailingHistoryById(chatId)
                .orElse(new MailingHistory());
        final String text = prepareTextForMessage(new MailingHistoryDto(mailingHistory));
        final List<ButtonData> callbackButtonDataList = buttonDataPreparer.prepare(CallbackButtonDataPreparerParam.onlyBack(MenuStepCommandType.CREATE_MAILING));

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, callbackButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.MAILING_HISTORY;
    }
}
