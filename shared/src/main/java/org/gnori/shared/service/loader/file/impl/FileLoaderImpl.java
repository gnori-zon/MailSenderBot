package org.gnori.shared.service.loader.file.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.gnori.data.flow.Result;
import org.gnori.shared.service.loader.binary.BinaryLoader;
import org.gnori.data.model.FileData;
import org.gnori.shared.service.loader.file.FileFailure;
import org.gnori.shared.service.loader.file.FileLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileLoaderImpl implements FileLoader {

    static final String FILENAME_PATTERN = "%s_%s";

    private final BinaryLoader binaryLoader;

    @Override
    public Result<FileSystemResource, FileFailure> loadFile(FileData fileData) {

        final String fileId = fileData.id();
        final String suffix = fileData.suffix();
        final String prefix = FILENAME_PATTERN.formatted(fileData.prefix(), fileData.name());

        return binaryLoader.loadBy(fileId)
                .mapFailure(loadFailure -> FileFailure.LOAD_FAILURE)
                .flatMapSuccess(bytes -> writeToTempFile(prefix, suffix, bytes));
    }

    private Result<FileSystemResource, FileFailure> writeToTempFile(
            String prefix,
            String suffix,
            byte @NonNull [] fileInByte
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
}
