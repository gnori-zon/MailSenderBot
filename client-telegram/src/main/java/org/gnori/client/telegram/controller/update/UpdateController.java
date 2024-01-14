package org.gnori.client.telegram.controller.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.service.MessageTypesDistributorService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Controller to call distributor
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class UpdateController {

    private final MessageTypesDistributorService distributorService;

    public void processUpdate(Update update) {

        switch (UpdateType.of(update)) {
            case EMPTY -> log.error("Received update is null");
            case UNSUPPORTED -> log.error("Unsupported message type is received: {}", update);
            case VALID -> distributorService.distribute(update);
        }
    }
}
