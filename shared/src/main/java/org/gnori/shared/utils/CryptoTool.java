package org.gnori.shared.utils;

import org.gnori.shared.flow.Result;

public interface CryptoTool {

    Result<String, CryptoFailure> encrypt(String value);
    Result<String, CryptoFailure> decrypt(String value);
}
