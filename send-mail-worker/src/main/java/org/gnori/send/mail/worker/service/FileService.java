package org.gnori.send.mail.worker.service;

import org.springframework.core.io.FileSystemResource;
import org.telegram.telegrambots.meta.api.objects.Document;

/**
 * Service for handling files
 */
public interface FileService {
    int processDoc(Long id);
    int processPhoto(Long id);
    FileSystemResource getFileSystemResource(Long id);

}
