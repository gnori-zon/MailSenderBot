package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.command.utils.preparers.text.TextPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.SimpleTextPreparerParam;
import org.gnori.client.telegram.service.message.MessageStorage;
import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.gnori.data.service.mailing.MailingHistoryService;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendCurrentMailCallbackCommand implements CallbackCommand {

    @Value("${spring.rabbitmq.exchange-name}")
    private String exchangeName;

    private final TextPreparer textPreparer;
    private final MessageStorage messageStorage;
    private final RabbitTemplate rabbitTemplate;
    private final BotMessageEditor botMessageEditor;
    private final MailingHistoryService mailingHistoryService;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        mailingHistoryService.updateStateMessageByAccountId(account.getId(), StateMessage.QUEUE);

        final Message messageToSend = messageStorage.getMessage(account.getId());
        rabbitTemplate.convertAndSend(exchangeName, null, messageToSend.withSendMode(SendMode.CURRENT_MAIL));
        messageStorage.clearMessage(account.getId());

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String textForWaiting = textPreparer.prepare(SimpleTextPreparerParam.BEFORE_SEND_TO_QUEUE_MESSAGE);

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, textForWaiting));

        final String text = textPreparer.prepare(SimpleTextPreparerParam.AFTER_SEND_TO_QUEUE_MESSAGE);
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, newCallbackButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.SEND_CURRENT_MAIL;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_START_MENU_ITEM,
                false
        );
    }
}
