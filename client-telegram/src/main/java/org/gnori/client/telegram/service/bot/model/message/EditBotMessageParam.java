package org.gnori.client.telegram.service.bot.model.message;

import org.gnori.client.telegram.service.bot.model.button.ButtonData;

import java.util.Collections;
import java.util.List;

public record EditBotMessageParam(
        long chatId,
        int messageId,
        String text,
        List<ButtonData> buttonDataList
) {

    public EditBotMessageParam(
            long chatId,
            int messageId,
            String text
    ) {
        this(chatId, messageId, text, Collections.emptyList());
    }
}
