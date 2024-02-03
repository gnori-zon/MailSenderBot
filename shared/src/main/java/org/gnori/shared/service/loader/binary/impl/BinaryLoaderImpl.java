package org.gnori.shared.service.loader.binary.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.shared.service.loader.binary.BinaryLoader;
import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.url.URLLoader;
import org.gnori.shared.service.loader.LoadFailure;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Component
@RequiredArgsConstructor
public class BinaryLoaderImpl implements BinaryLoader {

    static final int DEFAULT_CHUNK_SIZE = 262144;

    private final URLLoader URLLoader;

    @Value("${bot.token}")
    private String token;

    @Value("${service.file-info.uri}")
    private String fileInfoUri;

    @Value("${service.file-storage.uri}")
    private String fileStorageUri;

    @Override
    public Result<byte[], LoadFailure> loadBy(String fileId) {

        return loadFilePath(fileId)
                .flatMapSuccess(this::validateStatusCode)
                .flatMapSuccess(this::extractFilePath)
                .flatMapSuccess(this::loadBinary);
    }

    private Result<ResponseEntity<String>, LoadFailure> loadFilePath(String fileId) {

        final RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        try {

            final ResponseEntity<String> response = restTemplate.exchange(
                    fileInfoUri,
                    HttpMethod.GET,
                    request,
                    String.class,
                    token,
                    fileId
            );

            return Result.success(response);

        } catch (RestClientException e) {
            log.error("bad loadBytes filepath by id: {}, ex: {}", fileId, e.getLocalizedMessage());
            return Result.failure(LoadFailure.REST_CLIENT_EXCEPTION);
        }
    }

    private Result<ResponseEntity<String>, LoadFailure> validateStatusCode(ResponseEntity<String> response) {

        if (response.getStatusCode() == HttpStatus.OK) {
            return Result.success(response);
        }

        return Result.failure(LoadFailure.NOT_SUCCESS_STATUS_CODE);
    }

    private Result<String, LoadFailure> extractFilePath(ResponseEntity<String> response) {

        try {

            final JSONObject jsonObject = new JSONObject(response.getBody());
            final String filePath = jsonObject.getJSONObject("result").getString("file_path");

            return Result.success(filePath);

        } catch (JSONException e) {
            log.error("bad extract file path: {}", e.getLocalizedMessage());
            return Result.failure(LoadFailure.JSON_EXCEPTION);
        }
    }

    private Result<byte[], LoadFailure> loadBinary(String filePath) {

        return URLLoader.load(fullUri(filePath), DEFAULT_CHUNK_SIZE);
    }

    private String fullUri(String filePath) {

        return fileStorageUri
                .replace("{token}", token)
                .replace("{filePath}",filePath);
    }
}
