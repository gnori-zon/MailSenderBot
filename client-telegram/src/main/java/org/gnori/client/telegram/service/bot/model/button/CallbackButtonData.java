package org.gnori.client.telegram.service.bot.model.button;

public record CallbackButtonData(
        String text,
        String callbackData
) implements ButtonData {}
