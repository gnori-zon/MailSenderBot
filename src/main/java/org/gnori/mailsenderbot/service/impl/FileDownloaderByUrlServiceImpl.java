package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.service.FileDownloaderByUrlService;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.OptionalLong;

import static java.lang.String.format;
/**
 * Implementation service {@link FileDownloaderByUrlService}
 */
@Log4j
@Service
public class FileDownloaderByUrlServiceImpl implements FileDownloaderByUrlService {
    private static final String HEADER_RANGE = "Range";
    private static final String RANGE_FORMAT = "bytes=%d-%d";
    private static final String HEADER_CONTENT_LENGTH = "content-length";
    private static final String HTTP_METHOD_HEAD = "HEAD";
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final int HTTP_STATUS_PARTIAL_CONTENT = 206;
    static final String ERROR_TEXT_IO = "I/O error has occurred. ";

    private final HttpClient httpClient;
    private int maxAttempts;

    public FileDownloaderByUrlServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
    }

    private long contentLength(final String uri)
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest headRequest = HttpRequest
                .newBuilder(new URI(uri))
                .method(HTTP_METHOD_HEAD, HttpRequest.BodyPublishers.noBody())
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.ofString());

        OptionalLong contentLength = httpResponse
                .headers().firstValueAsLong(HEADER_CONTENT_LENGTH);

        return contentLength.orElse(0L);
    }

    @Override
    public Response download(final String uri, int firstBytePos, int lastBytePos)
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest
                .newBuilder(new URI(uri))
                .header(HEADER_RANGE, format(RANGE_FORMAT, firstBytePos, lastBytePos))
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return new Response(new BufferedInputStream(response.body()), response.statusCode(), response.headers());
    }

    @Override
    public byte[] download(final String uri, int chunkSize) throws URISyntaxException, IOException, InterruptedException {

        final int expectedLength = (int) contentLength(uri);
        int firstBytePos = 0;
        int lastBytePos = chunkSize - 1;
        var downloadedBytes = new byte[expectedLength];
        int downloadedLength = 0;

        int attempts = 1;

        while (downloadedLength < expectedLength && attempts < maxAttempts) {
            Response response;

            try {
                response = download(uri, firstBytePos, lastBytePos);
            } catch (IOException e) {
                attempts++;
                log.error(ERROR_TEXT_IO+ e);
                continue;
            }

            try (response.inputStream) {
                var chunkedBytes = response.inputStream.readAllBytes();
                downloadedLength += chunkedBytes.length;

                if (isPartial(response)) {
                    System.arraycopy(chunkedBytes, 0, downloadedBytes, firstBytePos, chunkedBytes.length);
                    firstBytePos = lastBytePos + 1;
                    lastBytePos = Math.min(lastBytePos + chunkSize, expectedLength - 1);
                }
            } catch (IOException e) {
                attempts++;
                log.error(ERROR_TEXT_IO+ e);
                continue;
            }
            attempts = 1; // reset attempts counter
        }
        if (attempts >= maxAttempts) {
            log.error("A file could not be downloaded. Number of attempts are exceeded.");
        }
        return downloadedBytes;
    }

    private boolean isPartial(Response response) {
        return response.status == HTTP_STATUS_PARTIAL_CONTENT;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public static class Response {
        final BufferedInputStream inputStream;
        final int status;
        final HttpHeaders headers;

        public Response(BufferedInputStream inputStream, int status, HttpHeaders headers) {
            this.inputStream = inputStream;
            this.status = status;
            this.headers = headers;
        }
    }
}
