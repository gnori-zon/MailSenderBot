package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.gnori.mailsenderbot.aop.LogExecutionTime;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileDownloaderByUrlService;
import org.gnori.mailsenderbot.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
/**
 * Implementation service {@link FileService}
 */
@Log4j
@Service
public class FileServiceImpl implements FileService {
    static final String BAD_RESPONSE_ERROR_TEXT = "Bad response: ";

    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final MessageRepository messageRepository;
    private final FileDownloaderByUrlService fileDownloaderByUrlService;

    public FileServiceImpl(MessageRepository messageRepository, FileDownloaderByUrlService fileDownloaderByUrlService) {
        this.messageRepository = messageRepository;
        this.fileDownloaderByUrlService = fileDownloaderByUrlService;
    }

    @LogExecutionTime
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

    @LogExecutionTime
    @Override
    public int processDoc(Long id) {
            var message = messageRepository.getMessage(id);
            var fileId = message.getDocAnnex().getFileId();
            ResponseEntity<String> response = getFilePath(fileId);
            if (response.getStatusCode() == HttpStatus.OK) {
                var fileInByte = getBinaryContent(response);

                message.setBinaryContentForAnnex(fileInByte);
                messageRepository.putMessage(id, message);
                return 1;
            }else {
                log.error(BAD_RESPONSE_ERROR_TEXT + response);
                return 0;
            }
    }

    @LogExecutionTime
    @Override
    public int processPhoto(Long id) {
        var message = messageRepository.getMessage(id);
        var telegramPhoto = message.getPhotoAnnex();
        var fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var fileInByte = getBinaryContent(response);

            message.setBinaryContentForAnnex(fileInByte);
            messageRepository.putMessage(id, message);
            return 1;
        }else {
            log.error(BAD_RESPONSE_ERROR_TEXT + response);
            return 0;
        }
    }

    @LogExecutionTime
    @Override
    public FileSystemResource getFileSystemResource(Long id) {
        var message = messageRepository.getMessage(id);
        var binaryContent = message.getBinaryContentForAnnex();
        var prefix = "";
        var suffix = "";
        if(message.getDocAnnex()!=null) {
            suffix = getSuffix(message.getDocAnnex().getFileName());
            prefix = "file_"+id;
        } else if (message.getPhotoAnnex()!=null) {
            suffix = ".jpeg";
            prefix = "photo_"+id;
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
