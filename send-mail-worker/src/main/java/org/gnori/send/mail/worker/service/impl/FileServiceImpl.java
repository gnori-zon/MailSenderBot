package org.gnori.send.mail.worker.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.gnori.send.mail.worker.aop.LogExecutionTime;
import org.gnori.send.mail.worker.service.FileService;
import org.gnori.shared.service.loader.url.URLLoader;
import org.gnori.data.model.Message;
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

    private final URLLoader URLLoader;

    public FileServiceImpl(URLLoader URLLoader) {
        this.URLLoader = URLLoader;
    }

    @LogExecutionTime
    @Override
    public int processDoc(Message message) {
            var fileId = message.getDocAnnex().getFileId();
            ResponseEntity<String> response = getFilePath(fileId);
            if (response.getStatusCode() == HttpStatus.OK) {
                var fileInByte = getBinaryContent(response);

                message.setBinaryContentForAnnex(fileInByte);
                return 1;
            }else {
                log.error(BAD_RESPONSE_ERROR_TEXT + response);
                return 0;
            }
    }

    @LogExecutionTime
    @Override
    public int processPhoto(Message message) {
        var telegramPhoto = message.getPhotoAnnex();
        var fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var fileInByte = getBinaryContent(response);

            message.setBinaryContentForAnnex(fileInByte);
            return 1;
        }else {
            log.error(BAD_RESPONSE_ERROR_TEXT + response);
            return 0;
        }
    }

    @LogExecutionTime
    @Override
    public FileSystemResource getFileSystemResource(Message message) {
        var binaryContent = message.getBinaryContentForAnnex();
        var prefix = "";
        var suffix = "";
        if(message.getDocAnnex()!=null) {
            suffix = getSuffix(message.getDocAnnex().getFileName());
            prefix = "file_"+message.getChatId();
        } else if (message.getPhotoAnnex()!=null) {
            suffix = ".jpeg";
            prefix = "photo_"+message.getChatId();
        }
        try{
            File temp = File.createTempFile(prefix, suffix);
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent);
            return new FileSystemResource(temp);
        }catch (IOException e){
            log.error(e);
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
            return URLLoader.loadBytes(fullUri, defaultChunkSize);
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
