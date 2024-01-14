package org.gnori.shared.service.loader.url;

public enum LoadFailure {
    NOT_SUCCESS_STATUS_CODE,
    REST_CLIENT_EXCEPTION,
    JSON_EXCEPTION,
    IO_EXCEPTION,
    URI_SYNTAX_EXCEPTION,
    INTERRUPTED_EXCEPTION,
    UNDEFINED
}
