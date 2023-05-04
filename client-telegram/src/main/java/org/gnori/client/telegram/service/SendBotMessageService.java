package org.gnori.client.telegram.service;

import org.gnori.client.telegram.controller.TelegramBot;
import java.util.List;

/**
 * Service for sending message in telegram
 */
public interface SendBotMessageService {
    void registerBot(TelegramBot telegramBot);
    void sendMessage(long chatId, String textToSend);
    void executeEditMessage( Long chatId,
                             Integer messageId,
                             String textForMessage,
                             List<List<String>> newCallbackData,
                             Boolean witBackButton);
    void executeEditMessage( Long chatId,
                             Integer messageId,
                             String textForMessage,
                             List<List<String>> newCallbackData,
                             List<List<String>> urls,
                             Boolean witBackButton);
    void createChangeableMessage(Long chatId,
                                 String textForMessage,
                                 List<List<String>> newCallbackData,
                                 Boolean witBackButton);
}
