package org.gnori.client.telegram.service.distribute;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageTypesDistributorService {
    void distribute(Update update);
}
