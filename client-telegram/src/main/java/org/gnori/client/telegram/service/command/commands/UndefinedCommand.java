package org.gnori.client.telegram.service.command.commands;

import lombok.AllArgsConstructor;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.BotMessageSender;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.message.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForLastForUnknownMessage;
import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForStartMessage;

@Component
@AllArgsConstructor
public class UndefinedCommand {

    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOld = prepareTextForLastForUnknownMessage();

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOld));

        final String text = prepareTextForStartMessage();
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_START_MENU_ITEM,
                false
        );
    }
}
