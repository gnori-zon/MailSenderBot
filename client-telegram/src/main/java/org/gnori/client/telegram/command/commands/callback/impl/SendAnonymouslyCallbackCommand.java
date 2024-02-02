package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.bot.SendBotMessageService;
import org.gnori.client.telegram.service.bot.model.CallbackButtonData;
import org.gnori.client.telegram.service.message.MessageStorageImpl;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.gnori.store.domain.service.mailing.MailingHistoryService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;

@Component
@RequiredArgsConstructor
public class SendAnonymouslyCallbackCommand implements CallbackCommand {

    @Value("${spring.rabbitmq.exchange-name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;
    private final SendBotMessageService sendBotMessageService;
    private final MailingHistoryService mailingHistoryService;
    private final MessageStorageImpl messageStorageImpl;
    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String textForWaiting = prepareTextForWaitingForConcreteSendingMessage();

        sendBotMessageService.editMessage(chatId, messageId, textForWaiting, Collections.emptyList(), false);


        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());
        final String text = prepareTextForSendCurrentAndAnonymouslyMessage();

        final Message messageToSend = messageStorageImpl.getMessage(account.getId());
        rabbitTemplate.convertAndSend(exchangeName, null, messageToSend.withSendMode(SendMode.ANONYMOUSLY));
        messageStorageImpl.clearMessage(account.getId());

        mailingHistoryService.updateStateMessageByAccountId(chatId, StateMessage.QUEUE);

        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, false);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.SEND_ANONYMOUSLY;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_START_MENU_ITEM,
                false
        );
    }
}
