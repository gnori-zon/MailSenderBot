package org.gnori.shared.service.loader.url.impl;

import lombok.extern.log4j.Log4j2;
import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.url.LoadFailure;
import org.gnori.shared.service.loader.url.URLLoader;
import org.gnori.shared.service.loader.url.URLLoaderResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.lang.String.format;

@Log4j2
@Service
public class URLLoaderImpl implements URLLoader {

    private static final String HEADER_RANGE = "Range";
    private static final String RANGE_FORMAT = "bytes=%d-%d";
    private static final String HEADER_CONTENT_LENGTH = "content-length";
    private static final String HTTP_METHOD_HEAD = "HEAD";
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final int HTTP_STATUS_PARTIAL_CONTENT = 206;
    static final String ERROR_TEXT_IO = "I/O error has occurred. ";

    private final HttpClient httpClient = defaultHttpClient();

    private HttpClient defaultHttpClient() {

        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Result<byte[], LoadFailure> load(String uri, int chunkSize) {

        try {
            return Result.success(loadBytes(uri, chunkSize));
        } catch (Exception exception) {
            return Result.failure(detectFailure(exception));
        }
    }

    private LoadFailure detectFailure(Exception exception) {

        if (exception instanceof IOException) {
            return LoadFailure.IO_EXCEPTION;
        } else if (exception instanceof URISyntaxException) {
            return LoadFailure.URI_SYNTAX_EXCEPTION;
        } else if (exception instanceof InterruptedException) {
            return LoadFailure.INTERRUPTED_EXCEPTION;
        } else {
            return LoadFailure.UNDEFINED;
        }
    }

    private byte[] loadBytes(final String uri, int chunkSize) throws URISyntaxException, IOException, InterruptedException {

        final int expectedLength = contentLength(uri);
        int firstBytePos = 0;
        int lastBytePos = chunkSize - 1;
        var downloadedBytes = new byte[expectedLength];
        int downloadedLength = 0;

        int attempts = 1;

        while (downloadedLength < expectedLength && attempts < DEFAULT_MAX_ATTEMPTS) {

            try {

                final URLLoaderResponse response = load(uri, firstBytePos, lastBytePos);

                try (final BufferedInputStream inputStream = response.inputStream()) {

                    final byte[] chunkedBytes = inputStream.readAllBytes();
                    downloadedLength += chunkedBytes.length;

                    if (isPartial(response)) {
                        System.arraycopy(chunkedBytes, 0, downloadedBytes, firstBytePos, chunkedBytes.length);
                        firstBytePos = lastBytePos + 1;
                        lastBytePos = Math.min(lastBytePos + chunkSize, expectedLength - 1);
                    }
                }
            } catch (URISyntaxException | IOException | InterruptedException e) {
                attempts++;
                log.error(ERROR_TEXT_IO + e);
                continue;
            }

            attempts = 1; // reset attempts counter
        }

        if (attempts >= DEFAULT_MAX_ATTEMPTS) {
            log.error("A file could not be downloaded. Number of attempts are exceeded.");
        }

        return downloadedBytes;
    }

    private int contentLength(final String uri) throws URISyntaxException, IOException, InterruptedException {

        final HttpRequest headRequest = contentLengthRequest(new URI(uri));

        final long length = httpClient.send(headRequest, HttpResponse.BodyHandlers.ofString())
                .headers()
                .firstValueAsLong(HEADER_CONTENT_LENGTH)
                .orElse(0L);

        return (int) length;
    }

    private HttpRequest contentLengthRequest(URI uri) {

        return HttpRequest
                .newBuilder(uri)
                .method(HTTP_METHOD_HEAD, HttpRequest.BodyPublishers.noBody())
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    public URLLoaderResponse load(final String uri, int firstBytePos, int lastBytePos) throws URISyntaxException, IOException, InterruptedException {

        final HttpRequest request = rangeRequest(new URI(uri), firstBytePos, lastBytePos);
        final HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return toUrlResponse(response);
    }

    private HttpRequest rangeRequest(URI uri, int firstBytePos, int lastBytePos) {

        return HttpRequest
                .newBuilder(uri)
                .header(HEADER_RANGE, format(RANGE_FORMAT, firstBytePos, lastBytePos))
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    private URLLoaderResponse toUrlResponse(HttpResponse<InputStream> response) {

        return new URLLoaderResponse(
                new BufferedInputStream(response.body()),
                response.statusCode(),
                response.headers()
        );
    }

    private boolean isPartial(URLLoaderResponse response) {
        return response.status() == HTTP_STATUS_PARTIAL_CONTENT;
    }
}
