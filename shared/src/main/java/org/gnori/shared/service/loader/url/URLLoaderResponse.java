package org.gnori.shared.service.loader.url;

import java.io.BufferedInputStream;
import java.net.http.HttpHeaders;

public record URLLoaderResponse(
        BufferedInputStream inputStream,
        int status,
        HttpHeaders headers
) {}
