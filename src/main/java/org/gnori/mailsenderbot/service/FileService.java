package org.gnori.mailsenderbot.service;

import org.springframework.core.io.FileSystemResource;

public interface FileService {
    int processDoc(Long id);
    int processPhoto(Long id);
    FileSystemResource getFileSystemResource(Long id);
}
