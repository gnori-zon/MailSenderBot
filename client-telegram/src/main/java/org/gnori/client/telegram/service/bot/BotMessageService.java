package org.gnori.client.telegram.service.bot;

import org.gnori.client.telegram.controller.TelegramBot;

@FunctionalInterface
public interface BotMessageService {
    void register(TelegramBot telegramBot);
}
