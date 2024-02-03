package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.message.MessageStorage;
import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.gnori.store.domain.service.mailing.MailingHistoryService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;

@Component
@RequiredArgsConstructor
public class SendCurrentMailCallbackCommand implements CallbackCommand {

    @Value("${spring.rabbitmq.exchange-name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;
    private final BotMessageEditor botMessageEditor;
    private final MessageStorage messageStorage;
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
        final String textForWaiting = prepareTextForWaitingForConcreteSendingMessage();

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, textForWaiting));

        final String text = prepareTextForSendCurrentAndAnonymouslyMessage();
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
