package org.gnori.client.telegram.service.impl.bot.model;

public record CallbackButtonData(
        String text,
        String callbackData
) implements ButtonData {}
