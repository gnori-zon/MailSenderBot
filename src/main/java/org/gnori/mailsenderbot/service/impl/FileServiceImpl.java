package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final MessageRepository messageRepository;

    public FileServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

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
                log.error("Bad response: "+ response);
                return 0;
            }
    }

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
            log.error("Bad response: "+ response);
            return 0;
        }
    }

    @Override
    public FileSystemResource getFileSystemResource(Long id) {
        var message = messageRepository.getMessage(id);
        var binaryContent = message.getBinaryContentForAnnex();
        var prefix = "";
        var suffix = "";
        if(message.getDocAnnex()!=null) {
            suffix = getSuffix(message.getDocAnnex().getFileName());
            prefix = "file:"+id;
        } else if (message.getPhotoAnnex()!=null) {
            suffix = ".jpeg";
            prefix = "photo:"+id;
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

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}",filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        }catch (MalformedURLException e){
            log.error(e);
        }

        //TODO подумать над оптимизацией
        if(urlObj!=null) {
            try (InputStream is = urlObj.openStream()) {
                return is.readAllBytes();
            } catch (IOException e) {
                log.error(e);
            }
        }
        return null;
    }
    private byte[] getBinaryContent(ResponseEntity<String> response){
        var filePath = getFilePath(response);
        return downloadFile(filePath);
    }
    private String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

}
