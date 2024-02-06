package org.gnori.shared.crypto.impl;

import org.apache.commons.codec.binary.Base64;
import org.gnori.data.flow.Result;
import org.gnori.shared.crypto.CryptoFailure;
import org.gnori.shared.crypto.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class CryptoToolImpl implements CryptoTool {

    private static final String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private static final String CHARSET = "UTF-8";
    private static final String ALGORITHM_NAME = "AES";

    @Value("${cipher.key}")
    private String key;
    @Value("${cipher.initVector}")
    private String initVector;

    @Override
    public Result<String, CryptoFailure> encrypt(String value) {

        return getDefaultCipher(Cipher.ENCRYPT_MODE)
                .flatMapSuccess(cipher -> doFinalWith(cipher, value.getBytes()))
                .mapSuccess(Base64::encodeBase64String);
    }

    @Override
    public Result<String, CryptoFailure> decrypt(String encrypted) {

        return getDefaultCipher(Cipher.DECRYPT_MODE)
                .flatMapSuccess(cipher -> doFinalWith(cipher, Base64.decodeBase64(encrypted)))
                .mapSuccess(String::new);
    }

    private Result<Cipher, CryptoFailure> getDefaultCipher(int cryptoMode) {

        try {

            final IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes(CHARSET));
            final SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM_NAME);

            final Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(cryptoMode, secretKeySpec, ivParameterSpec);

            return Result.success(cipher);

        } catch (InvalidKeyException e) {
            return Result.failure(CryptoFailure.INVALID_KEY_EXCEPTION);
        } catch (NoSuchPaddingException e) {
            return Result.failure(CryptoFailure.NO_SUCH_PADDING_EXCEPTION);
        } catch (NoSuchAlgorithmException e) {
            return Result.failure(CryptoFailure.NO_SUCH_ALGORITHM_EXCEPTION);
        } catch (UnsupportedEncodingException e) {
            return Result.failure(CryptoFailure.UNSUPPORTED_ENCODING_EXCEPTION);
        } catch (InvalidAlgorithmParameterException e) {
            return Result.failure(CryptoFailure.INVALID_ALGORITHM_PARAMETER_EXCEPTION);
        }
    }

    private Result<byte[], CryptoFailure> doFinalWith(Cipher cipher, byte[] bytes) {

        try {

            return Result.success(cipher.doFinal(bytes));

        } catch (IllegalBlockSizeException e) {
            return Result.failure(CryptoFailure.ILLEGAL_BLOCK_SIZE_EXCEPTION);
        } catch (BadPaddingException e) {
            return Result.failure(CryptoFailure.BAD_PADDING_EXCEPTION);
        }
    }
}
