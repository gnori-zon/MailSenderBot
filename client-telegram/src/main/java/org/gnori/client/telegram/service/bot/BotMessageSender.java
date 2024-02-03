package org.gnori.client.telegram.service.bot;

import org.gnori.client.telegram.service.bot.model.message.SendBotMessageParam;

@FunctionalInterface
public interface BotMessageSender {
    void send(SendBotMessageParam param);
}
