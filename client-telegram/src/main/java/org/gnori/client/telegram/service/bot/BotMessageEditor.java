package org.gnori.client.telegram.service.bot;

import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;

@FunctionalInterface
public interface BotMessageEditor {
    void edit(EditBotMessageParam param);
}
