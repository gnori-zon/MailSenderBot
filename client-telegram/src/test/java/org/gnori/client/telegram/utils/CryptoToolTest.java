package org.gnori.client.telegram.utils;

import lombok.SneakyThrows;
import org.gnori.shared.utils.CryptoTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Unit-level testing for CryptoTool")
public class CryptoToolTest {
    @SneakyThrows
    @Test
    public void testEncryptionValidity() throws NoSuchFieldException {
        var initVector = "0@cPfrO1x3rx39x!";
        var key = "!RWr21aXr098x5r@";
        CryptoTool cryptoTool = new CryptoTool();
        var fieldInitVector = cryptoTool.getClass().getDeclaredField("initVector");
        fieldInitVector.setAccessible(true);
        var fieldKey = cryptoTool.getClass().getDeclaredField("key");
        fieldKey.setAccessible(true);
        fieldKey.set(cryptoTool,key);
        fieldInitVector.set(cryptoTool,initVector);
        var word = "password";
        Assertions.assertEquals(word, cryptoTool.decrypt(cryptoTool.encrypt(word)));
    }
}
