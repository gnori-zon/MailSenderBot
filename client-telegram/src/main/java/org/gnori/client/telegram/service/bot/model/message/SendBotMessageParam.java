package org.gnori.client.telegram.service.bot.model.message;

import org.gnori.client.telegram.service.bot.model.button.ButtonData;

import java.util.Collections;
import java.util.List;

public record SendBotMessageParam(
        long chatId,
        String text,
        List<ButtonData> buttonDataList
) {

    public SendBotMessageParam(
            long chatId,
            String text
    ) {
        this(chatId, text, Collections.emptyList());
    }
}
