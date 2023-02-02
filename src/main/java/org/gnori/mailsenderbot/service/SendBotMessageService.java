package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.controller.TelegramBot;

import java.util.List;

public interface SendBotMessageService {
    void registerBot(TelegramBot telegramBot);
    void sendMessage(long chatId, String textToSend);
    void executeEditMessage( Long chatId,
                             Integer messageId,
                             String textForMessage,
                             List<List<String>> newCallbackData,
                             Boolean witBackButton);
    void createChangeableMessage(Long chatId,
                                 String textForMessage,
                                 List<List<String>> newCallbackData,
                                 Boolean witBackButton);
}
