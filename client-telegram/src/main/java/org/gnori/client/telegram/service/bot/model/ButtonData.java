package org.gnori.client.telegram.service.bot.model;

public sealed interface ButtonData
        permits UrlButtonData, CallbackButtonData  {
    String text();
}
