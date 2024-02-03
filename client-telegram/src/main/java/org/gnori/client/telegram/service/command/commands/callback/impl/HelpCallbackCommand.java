package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.url.UrlButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.url.UrlButtonDataPresetType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForHelpMessage;

@Component
@RequiredArgsConstructor
public class HelpCallbackCommand implements CallbackCommand {

    private final BotMessageEditor botMessageEditor;
    private final ButtonDataPreparer<ButtonData, UrlButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForHelpMessage();
        final List<ButtonData> urlButtonDataList = buttonDataPreparer.prepare(urlButtonDataPreparerParamOf());

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, urlButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.HELP;
    }

    private UrlButtonDataPreparerParam urlButtonDataPreparerParamOf() {
        return new UrlButtonDataPreparerParam(UrlButtonDataPresetType.HELP_MAIL_CONFIGURATION, MenuStepCommandType.SETUP_SETTINGS_ITEM, true);
    }
}
