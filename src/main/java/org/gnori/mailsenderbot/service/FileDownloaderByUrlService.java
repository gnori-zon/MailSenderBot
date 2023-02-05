package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.service.impl.FileDownloaderByUrlServiceImpl;

import java.io.IOException;
import java.net.URISyntaxException;


public interface FileDownloaderByUrlService {
    byte[] download(String uri, int chunkSize) throws URISyntaxException, IOException, InterruptedException;
    FileDownloaderByUrlServiceImpl.Response download(String uri, int firstBytePos, int lastBytePos) throws URISyntaxException, IOException, InterruptedException;
}
