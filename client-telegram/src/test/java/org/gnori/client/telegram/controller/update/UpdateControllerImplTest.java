package org.gnori.client.telegram.controller.update;

import org.gnori.client.telegram.service.distribute.MessageTypesDistributorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

@DisplayName("Unit-level testing for UpdateControllerImpl")
class UpdateControllerImplTest {

    private MessageTypesDistributorService mockDistributorService = Mockito.mock(MessageTypesDistributorService.class);
    private final UpdateController updateController = new UpdateControllerImpl(mockDistributorService);

    @Test
    void successUpdate() {

        final Update update = new Update();
        updateController.process(update);

        Mockito.verify(mockDistributorService).distribute(update);
    }

    @Test
    void failureUpdate() {

        updateController.process(null);

        Mockito.verify(mockDistributorService, VerificationModeFactory.times(0)).distribute(Mockito.any());
    }
}