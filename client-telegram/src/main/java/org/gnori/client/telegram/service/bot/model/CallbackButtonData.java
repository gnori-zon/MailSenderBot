package org.gnori.client.telegram.service.bot.model;

public record CallbackButtonData(
        String text,
        String callbackData
) implements ButtonData {}
