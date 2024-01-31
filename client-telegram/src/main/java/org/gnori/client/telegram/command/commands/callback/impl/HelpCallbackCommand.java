package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.bot.model.UrlButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.url.UrlButtonDataPresetType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForHelpMessage;

@Component
@RequiredArgsConstructor
public class HelpCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final ButtonDataPreparer<UrlButtonData, UrlButtonDataPresetType> urlButtonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForHelpMessage();
        final List<UrlButtonData> urlButtonDataList = urlButtonDataPreparer.prepare(UrlButtonDataPresetType.HELP_MAIL_CONFIGURATION);

        sendBotMessageService.editMessage(chatId, messageId, text, Collections.emptyList(), urlsData, true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.HELP;
    }


}
