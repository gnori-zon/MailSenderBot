package org.gnori.client.telegram.service.bot.model;

public record UrlButtonData(
        String text,
        String url
) implements ButtonData {}
