package org.gnori.shared.service;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Service for downloading files for url
 */
public interface FileDownloaderByUrlService {
    byte[] download(String uri, int chunkSize) throws URISyntaxException, IOException, InterruptedException;
    FileDownloaderByUrlServiceImpl.Response download(String uri, int firstBytePos, int lastBytePos) throws URISyntaxException, IOException, InterruptedException;
}
