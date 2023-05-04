package org.gnori.client.telegram.service;

import org.springframework.core.io.FileSystemResource;
import org.telegram.telegrambots.meta.api.objects.Document;

/**
 * Service for handling files
 */
public interface FileService {

    FileSystemResource processMail(Long id, Document mailDoc);

}
