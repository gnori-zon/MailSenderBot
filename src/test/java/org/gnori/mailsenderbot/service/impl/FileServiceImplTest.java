package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.FileDownloaderByUrlService;
import org.gnori.mailsenderbot.service.impl.FileServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

@DisplayName("Unit-level testing for FileServiceImpl")
public class FileServiceImplTest {
    FileServiceImpl fileServiceImpl;
    private MessageRepository messageRepository;
    private FileDownloaderByUrlService fileDownloaderByUrlService;

    @BeforeEach
    public void init(){
        messageRepository = Mockito.mock(MessageRepository.class);
        fileDownloaderByUrlService = Mockito.mock(FileDownloaderByUrlService.class);
        fileServiceImpl = new FileServiceImpl(messageRepository, fileDownloaderByUrlService);
    }

    @Test
    public void getFileSystemResourcePositiveFile(){
        var id = 12;
        var firstPartName = "file";
        var secondPartName = ".format";
        var fileName = firstPartName+secondPartName;

        var message =Mockito.mock(Message.class);
        var doc = new Document();
        doc.setFileName(fileName);

        Mockito.when(messageRepository.getMessage((long)id)).thenReturn(message);
        Mockito.when(message.getDocAnnex()).thenReturn(doc);
        Mockito.when(message.getBinaryContentForAnnex()).thenReturn(new byte[]{101});

        var result = fileServiceImpl.getFileSystemResource((long)id);

        Assertions.assertTrue(result!=null &&
                result.getFilename().contains(firstPartName +"_"+ id) &&
                result.getFilename().contains(secondPartName));
    }

    @Test
    public void getFileSystemResourcePositivePhoto(){
        var id = 12;
        var firstPartName = "photo";
        var secondPartName = ".jpeg";
        var message =Mockito.mock(Message.class);
        var photo = new PhotoSize();

        Mockito.when(messageRepository.getMessage((long)id)).thenReturn(message);
        Mockito.when(message.getDocAnnex()).thenReturn(null);
        Mockito.when(message.getPhotoAnnex()).thenReturn(photo);
        Mockito.when(message.getBinaryContentForAnnex()).thenReturn(new byte[]{101});

        var result = fileServiceImpl.getFileSystemResource((long)id);

        Mockito.verify(message).getDocAnnex();
        Mockito.verify(message).getPhotoAnnex();
        Assertions.assertTrue(result!=null &&
                result.getFilename().contains(firstPartName+"_"+id) &&
                result.getFilename().contains(secondPartName));
    }

}
