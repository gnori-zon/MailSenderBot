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
import org.gnori.client.telegram.service.command.utils.preparers.text.TextPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.PatternTextPreparerParam;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.data.service.mailing.MailingHistoryService;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.MailingHistory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MailingHistoryCallbackCommand implements CallbackCommand {

    private final TextPreparer textPreparer;
    private final BotMessageEditor botMessageEditor;
    private final MailingHistoryService mailingHistoryService;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final MailingHistoryDto mailingHistoryDto = mailingHistoryService.getMailingHistoryById(chatId)
                .map(MailingHistoryDto::new)
                .orElseGet(() -> new MailingHistoryDto(new MailingHistory()));

        final String text = textPreparer.prepare(PatternTextPreparerParam.mailingHistory(mailingHistoryDto));
        final List<ButtonData> callbackButtonDataList = buttonDataPreparer.prepare(CallbackButtonDataPreparerParam.onlyBack(MenuStepCommandType.CREATE_MAILING));

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, callbackButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.MAILING_HISTORY;
    }
}
