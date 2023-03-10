package org.gnori.mailsenderbot.controller;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Controller to call distributor
 */
@Log4j
@Component
public class UpdateController {
    private final MessageTypesDistributorService distributorService;

    public UpdateController(MessageTypesDistributorService distributorService) {
        this.distributorService = distributorService;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage() || update.hasCallbackQuery()) {
            distributorService.distributeMessageByType(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }

    }
}

