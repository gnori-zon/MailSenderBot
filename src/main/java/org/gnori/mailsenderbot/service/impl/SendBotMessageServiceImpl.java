package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.controller.TelegramBot;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {
    static final String ERROR_TEXT = "Error occurred: ";
    private TelegramBot telegramBot;

    @Override
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setParseMode("Markdown");

        executeMessage(sendMessage);
    }

    @Override
    public void executeEditMessage(Long chatId,
                                   Integer messageId,
                                   String textForMessage,
                                   List<List<String>> newCallbackData, Boolean witBackButton) {
        var markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(Collections.emptyList());
        if(!newCallbackData.isEmpty()){
            markupInline = newInlineKeyboardMarkupColumn(newCallbackData);
        }

        var message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(textForMessage)
                .parseMode("Markdown")
                .replyMarkup(markupInline)
                .build();

        if(witBackButton){
            addBackCallBackData(message);
        }

        executeMessage(message);

    }

    @Override
    public void createChangeableMessage(Long chatId,
                                        String textForMessage,
                                        List<List<String>> newCallbackData) {
        var markupInline = new InlineKeyboardMarkup();
        if(!newCallbackData.isEmpty()){
            markupInline = newInlineKeyboardMarkupColumn(newCallbackData);
        }

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(textForMessage)
                .parseMode("Markdown")
                .replyMarkup(markupInline)
                .build();

        executeMessage(message);
    }

    private InlineKeyboardMarkup newInlineKeyboardMarkupColumn(List<List<String>> newCallbackData) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for(var callbackData : newCallbackData.get(0)){
            var index = newCallbackData.get(0).indexOf(callbackData);
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var buttonIntermediate = new InlineKeyboardButton();
            buttonIntermediate.setCallbackData(newCallbackData.get(0).get(index));
            buttonIntermediate.setText(newCallbackData.get(1).get(index));
            rowInline.add(buttonIntermediate);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private void executeMessage(SendMessage message) {
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    private void executeMessage(EditMessageText message) {
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void addBackCallBackData(EditMessageText message){
        List<List<InlineKeyboardButton>> ROWS_INLINE = new ArrayList<>();
        var markupInline = message.getReplyMarkup();
        var rowsInline = !(markupInline.getKeyboard().isEmpty()) ? markupInline.getKeyboard() : ROWS_INLINE ;

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var buttonIntermediate = new InlineKeyboardButton();
        buttonIntermediate.setCallbackData("BACK");
        buttonIntermediate.setText("Назад");
        rowInline.add(buttonIntermediate);
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }


}
