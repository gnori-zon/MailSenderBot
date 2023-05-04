package org.gnori.send.mail.worker.service.impl;

import org.gnori.data.model.Message;
import org.gnori.shared.service.FileDownloaderByUrlService;
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
    private FileDownloaderByUrlService fileDownloaderByUrlService;

    @BeforeEach
    public void init(){
        fileDownloaderByUrlService = Mockito.mock(FileDownloaderByUrlService.class);
        fileServiceImpl = new FileServiceImpl(fileDownloaderByUrlService);
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

        Mockito.when(message.getChatId()).thenReturn(12L);
        Mockito.when(message.getDocAnnex()).thenReturn(doc);
        Mockito.when(message.getBinaryContentForAnnex()).thenReturn(new byte[]{101});

        var result = fileServiceImpl.getFileSystemResource(message);

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

        Mockito.when(message.getChatId()).thenReturn(12L);
        Mockito.when(message.getDocAnnex()).thenReturn(null);
        Mockito.when(message.getPhotoAnnex()).thenReturn(photo);
        Mockito.when(message.getBinaryContentForAnnex()).thenReturn(new byte[]{101});

        var result = fileServiceImpl.getFileSystemResource(message);

        Mockito.verify(message).getDocAnnex();
        Mockito.verify(message).getPhotoAnnex();
        Assertions.assertTrue(result!=null &&
                result.getFilename().contains(firstPartName+"_"+id) &&
                result.getFilename().contains(secondPartName));
    }

}
