package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.controller.TelegramBot;

public interface SendBotMessageService {
    void registerBot(TelegramBot telegramBot);
    void sendMessages(long chatId, String textToSend);
}
