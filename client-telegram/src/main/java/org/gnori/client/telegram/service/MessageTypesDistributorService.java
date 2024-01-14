package org.gnori.client.telegram.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Service for distributing messages by type
 */
public interface MessageTypesDistributorService {
    void distribute(Update update);
}
