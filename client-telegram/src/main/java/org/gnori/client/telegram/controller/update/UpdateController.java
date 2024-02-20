package org.gnori.client.telegram.controller.update;

import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface UpdateController {
    void process(Update update);
}
