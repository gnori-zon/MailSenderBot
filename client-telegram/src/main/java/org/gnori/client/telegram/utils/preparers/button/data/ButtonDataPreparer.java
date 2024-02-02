package org.gnori.client.telegram.utils.preparers.button.data;

import org.gnori.client.telegram.service.bot.model.ButtonData;

import java.util.List;

public interface ButtonDataPreparer<R extends ButtonData, T> {
    List<R> prepare(T type);
}
