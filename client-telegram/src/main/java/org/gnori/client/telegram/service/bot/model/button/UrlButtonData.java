package org.gnori.client.telegram.service.bot.model.button;

public record UrlButtonData(
        String text,
        String url
) implements ButtonData {}
