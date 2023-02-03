package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.service.impl.FileDownloaderByUrlImpl;

import java.io.IOException;
import java.net.URISyntaxException;


public interface FileDownloaderByUrl {
    byte[] download(String uri, int chunkSize) throws URISyntaxException, IOException, InterruptedException;
    FileDownloaderByUrlImpl.Response download(String uri, int firstBytePos, int lastBytePos) throws URISyntaxException, IOException, InterruptedException;
}
