package org.gnori.send.mail.worker.service;

import org.gnori.data.model.Message;
import org.springframework.core.io.FileSystemResource;

/**
 * Service for handling files
 */
public interface FileService {
    int processDoc(Message message);
    int processPhoto(Message message);
    FileSystemResource getFileSystemResource(Message message);

}
