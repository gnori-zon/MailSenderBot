package org.gnori.client.telegram.service.bot.message;

import org.gnori.client.telegram.service.bot.message.model.message.SendBotMessageParam;

@FunctionalInterface
public interface BotMessageSender {
    void send(SendBotMessageParam param);
}
