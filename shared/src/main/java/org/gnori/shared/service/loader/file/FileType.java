package org.gnori.shared.service.loader.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    PHOTO("photo", ".jpeg"),
    DOCUMENT("document", "");

    private final String prefix;
    private final String suffix;
}
