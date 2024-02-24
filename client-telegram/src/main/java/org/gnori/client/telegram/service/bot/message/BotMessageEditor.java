package org.gnori.client.telegram.service.bot.message;

import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;

@FunctionalInterface
public interface BotMessageEditor {
    void edit(EditBotMessageParam param);
}
