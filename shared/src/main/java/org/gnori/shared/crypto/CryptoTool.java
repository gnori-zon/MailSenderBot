package org.gnori.shared.crypto;

import org.gnori.data.flow.Result;

public interface CryptoTool {

    Result<String, CryptoFailure> encrypt(String value);
    Result<String, CryptoFailure> decrypt(String value);
}
