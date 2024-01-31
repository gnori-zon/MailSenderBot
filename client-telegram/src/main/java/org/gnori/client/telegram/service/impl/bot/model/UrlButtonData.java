package org.gnori.client.telegram.service.impl.bot.model;

public record UrlButtonData(
        String text,
        String url
) implements ButtonData {}
