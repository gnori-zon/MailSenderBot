package org.gnori.client.telegram.service.bot.message.model.button;

public record UrlButtonData(
        String text,
        String url
) implements ButtonData {}
