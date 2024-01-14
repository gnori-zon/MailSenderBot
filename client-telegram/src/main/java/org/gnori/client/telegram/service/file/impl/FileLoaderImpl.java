package org.gnori.client.telegram.service.file.impl;

import java.io.File;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.gnori.client.telegram.service.file.FileFailure;
import org.gnori.client.telegram.service.file.FileLoader;
import org.gnori.client.telegram.service.load.binary.BinaryLoader;
import org.gnori.shared.flow.Result;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;

import javax.validation.constraints.NotNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileLoaderImpl implements FileLoader {

    static final String PREFIX_PATTERN = "file_%s";

    private final BinaryLoader binaryLoader;

    @Override
    public Result<FileSystemResource, FileFailure> loadFile(Long id, Document mailDoc) {

        final String fileId = mailDoc.getFileId();
        final String suffix = getSuffix(mailDoc.getFileName());
        final String prefix = PREFIX_PATTERN.formatted(id);

        return binaryLoader.loadBy(fileId)
                .mapFailure(loadFailure -> FileFailure.LOAD_FAILURE)
                .flatMapSuccess(bytes -> writeToTempFile(prefix, suffix, bytes));
    }

    private Result<FileSystemResource, FileFailure> writeToTempFile(
            String prefix,
            String suffix,
            @NotNull byte[] fileInByte
    ) {
        try {

            final File temp = File.createTempFile(prefix, suffix);
            temp.deleteOnExit();

            FileUtils.writeByteArrayToFile(temp, fileInByte);

            return Result.success(new FileSystemResource(temp));
        } catch (IOException e) {
            log.error("bad write to file: {}", e.getLocalizedMessage());
            return Result.failure(FileFailure.IO_FAILURE);
        }
    }


    private String getSuffix(String fileName) {

        final int indexPoint = fileName.indexOf(".");
        return fileName.substring(indexPoint);
    }
}
