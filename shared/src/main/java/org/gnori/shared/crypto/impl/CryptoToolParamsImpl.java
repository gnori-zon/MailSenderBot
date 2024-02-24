package org.gnori.shared.crypto.impl;

import org.gnori.shared.crypto.CryptoToolParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("cipher")
public record CryptoToolParamsImpl(
        String key,
        String initVector
) implements CryptoToolParams {}
