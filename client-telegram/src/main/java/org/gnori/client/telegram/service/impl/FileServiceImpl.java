package org.gnori.client.telegram.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.gnori.client.telegram.service.FileService;
import org.gnori.shared.service.FileDownloaderByUrlService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;

/**
 * Implementation service {@link FileService}
 */
@Log4j2
@Service
public class FileServiceImpl implements FileService {
    static final String BAD_RESPONSE_ERROR_TEXT = "Bad response: ";

    @Value("${bot.token}")
    private String token;
    @Value("${service.file-info.uri}")
    private String fileInfoUri;
    @Value("${service.file-storage.uri}")
    private String fileStorageUri;
    private final FileDownloaderByUrlService fileDownloaderByUrlService;

    public FileServiceImpl(FileDownloaderByUrlService fileDownloaderByUrlService) {
        this.fileDownloaderByUrlService = fileDownloaderByUrlService;
    }

    @Override
    public FileSystemResource processMail(Long id, Document mailDoc) {
        var fileId = mailDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var fileInByte = getBinaryContent(response);
            var suffix = getSuffix(mailDoc.getFileName());
            var prefix = "file_"+id;
            try{
                File temp = File.createTempFile(prefix, suffix);
                temp.deleteOnExit();
                FileUtils.writeByteArrayToFile(temp, fileInByte);
                return new FileSystemResource(temp);
            }catch (IOException e){
                log.error(e);
                return null;
            }
        } else {
            log.error(BAD_RESPONSE_ERROR_TEXT + response);
            return null;
        }
    }


    private String getSuffix(String fileName) {
        var indexPoint = fileName.indexOf(".");
        return fileName.substring(indexPoint);
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) throws  URISyntaxException,IOException,InterruptedException{
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}",filePath);
        var defaultChunkSize = 262144;
            return fileDownloaderByUrlService.download(fullUri, defaultChunkSize);
    }
    private byte[] getBinaryContent(ResponseEntity<String> response){
        var filePath = getFilePath(response);
        try {
            return downloadFile(filePath);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return null;
        }

    }
    private String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

}
