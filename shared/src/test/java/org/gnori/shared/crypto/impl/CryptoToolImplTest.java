package org.gnori.shared.crypto.impl;

import org.gnori.shared.crypto.CryptoFailure;
import org.gnori.shared.crypto.CryptoTool;
import org.gnori.shared.crypto.CryptoToolParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-level testing for CryptoToolImpl")
class CryptoToolImplTest {

    private final CryptoToolParams invalidCryptoToolParams = new  CryptoToolParamsImpl(
            "799EE268FA9C1",
            "AB933D1D31991"
    );

    private final CryptoToolParams validCryptoToolParams = new  CryptoToolParamsImpl(
            "F-JaNdRgUjXn2r5u",
            "hVmYp3s6v9y$B&Em"
    );

    @Test
    void successEncryptAndDecrypt() {

        final String expected = "mock-test";
        final CryptoTool cryptoTool = getValidCryptoTool();
        final AtomicBoolean isSuccessEncrypt = new AtomicBoolean();
        final AtomicBoolean isSuccessDecrypt = new AtomicBoolean();

        cryptoTool.encrypt(expected)
                .doIfSuccess(encrypted -> {
                    isSuccessEncrypt.set(true);
                    Assertions.assertNotEquals(expected, encrypted);
                })
                .flatMapSuccess(cryptoTool::decrypt)
                .doIfSuccess(decrypted -> {
                    isSuccessDecrypt.set(true);
                    Assertions.assertEquals(expected, decrypted);
                });

        Assertions.assertTrue(isSuccessEncrypt.get());
        Assertions.assertTrue(isSuccessDecrypt.get());
    }

    @Test
    void failureEncryptAndDecrypt() {

        final String value = "mock-test";
        final CryptoTool cryptoTool = getInvalidCryptoTool();
        final AtomicBoolean isFailureEncrypt = new AtomicBoolean();
        final AtomicBoolean isFailureDecrypt = new AtomicBoolean();

        cryptoTool.encrypt(value)
                .doIfFailure(failure -> {
                    isFailureEncrypt.set(true);
                    Assertions.assertEquals(CryptoFailure.INVALID_ALGORITHM_PARAMETER_EXCEPTION, failure);
                });

        cryptoTool.decrypt(value)
                .doIfFailure(failure -> {
                    isFailureDecrypt.set(true);
                    Assertions.assertEquals(CryptoFailure.INVALID_ALGORITHM_PARAMETER_EXCEPTION, failure);
                });

        Assertions.assertTrue(isFailureEncrypt.get());
        Assertions.assertTrue(isFailureDecrypt.get());
    }

    private CryptoTool getValidCryptoTool() {
        return new CryptoToolImpl(validCryptoToolParams);
    }

    private CryptoTool getInvalidCryptoTool() {
        return new CryptoToolImpl(invalidCryptoToolParams);
    }
}