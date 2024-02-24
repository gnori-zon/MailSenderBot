package org.gnori.shared.service.loader.file;

import org.gnori.data.model.FileData;
import org.gnori.data.flow.Result;
import org.springframework.core.io.FileSystemResource;

/**
 * Service for handling files
 */
public interface FileLoader {

    Result<FileSystemResource, FileFailure> loadFile(FileData fileData);
}
