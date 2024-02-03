package org.gnori.client.telegram.service.bot.model.button;

public sealed interface ButtonData
        permits UrlButtonData, CallbackButtonData  {
    String text();
}
