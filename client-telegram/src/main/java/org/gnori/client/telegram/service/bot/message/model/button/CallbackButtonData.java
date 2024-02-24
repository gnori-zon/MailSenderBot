package org.gnori.client.telegram.service.bot.message.model.button;

public record CallbackButtonData(
        String text,
        String callbackData
) implements ButtonData {}
