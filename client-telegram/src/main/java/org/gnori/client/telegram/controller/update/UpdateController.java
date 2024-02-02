package org.gnori.client.telegram.controller.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.service.distribute.MessageTypesDistributorService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

/**
 * Controller to call distributor
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class UpdateController {

    private static final String UPDATE_IS_NULL = "Received update is null";

    private final MessageTypesDistributorService distributorService;

    public void processUpdate(Update update) {

        Optional.ofNullable(update)
                .ifPresentOrElse(
                        distributorService::distribute,
                        () -> log.error(UPDATE_IS_NULL)
                );
    }
}
