package org.gnori.client.telegram.service.file;

import org.gnori.shared.flow.Result;
import org.springframework.core.io.FileSystemResource;
import org.telegram.telegrambots.meta.api.objects.Document;

/**
 * Service for handling files
 */
public interface FileLoader {

    Result<FileSystemResource, FileFailure> loadFile(Long id, Document mailDoc);
}
