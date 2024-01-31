package org.gnori.client.telegram.service.impl.bot.model;

public sealed interface ButtonData
        permits UrlButtonData, CallbackButtonData  {
    String text();
}
