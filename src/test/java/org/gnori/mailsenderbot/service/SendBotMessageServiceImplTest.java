package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.controller.TelegramBot;
import org.gnori.mailsenderbot.service.impl.SendBotMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForProfileMessage;

@DisplayName("Unit-level testing for SendBotMessageServiceImpl")
public class SendBotMessageServiceImplTest {
    private TelegramBot telegramBot;
    private SendBotMessageServiceImpl sendBotMessageService;
    @BeforeEach
    public void init(){
        telegramBot = Mockito.mock(TelegramBot.class);
        sendBotMessageService = new SendBotMessageServiceImpl();
        sendBotMessageService.registerBot(telegramBot);
    }

    @Test
    public void sendMessage() throws TelegramApiException {
        var chatId = 12L;
        var textToSend = "text";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setParseMode("Markdown");

        sendBotMessageService.sendMessage(chatId,textToSend);

        Mockito.verify(telegramBot).execute(sendMessage);
    }

    @Test
    public void executeEditMessage() throws TelegramApiException {
        var chatId = 12L;
        var textToSend = "text";
        var messageId = 12;
        List<List<String>> newCallbackData = Collections.emptyList();
        var withBack = true;
        var markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var buttonIntermediate = new InlineKeyboardButton();
        buttonIntermediate.setCallbackData("BACK");
        buttonIntermediate.setText("Назад");
        rowInline.add(buttonIntermediate);
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        var message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(textToSend)
                .parseMode("Markdown")
                .replyMarkup(markupInline)
                .build();

        sendBotMessageService.executeEditMessage(chatId,messageId,textToSend,newCallbackData,withBack);

        Mockito.verify(telegramBot).execute(message);

    }

    @Test
    public void createChangeableMessage() throws TelegramApiException {
        var chatId = 12L;
        var textToSend = "text";
        var messageId = 12;
        List<List<String>> newCallbackData = Collections.emptyList();
        var withBack = true;
        var markupInline = new InlineKeyboardMarkup();

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(textToSend)
                .parseMode("Markdown")
                .replyMarkup(markupInline)
                .build();
            sendBotMessageService.createChangeableMessage(chatId,textToSend,newCallbackData,withBack);

            Mockito.verify(telegramBot).execute(message);

    }

}
