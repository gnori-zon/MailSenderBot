package org.gnori.mailsenderbot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

@DisplayName("Unit-level testing for UtilsCommand")
class UtilsCommandTest {

    @Test
    void validateMailPositiveTest() {
        var rawValue = "mailasd@mail.com";
        Assertions.assertTrue(UtilsCommand.validateMail(rawValue));
    }
    @Test
    void validateMailNegativeTest() {
        var rawValue = "mail!@#asd@mail.com";
        Assertions.assertFalse(UtilsCommand.validateMail(rawValue));
    }

    @Test
    void getSimpleDateFormat() {
        var format = "yyyy-MM-dd HH:mm:ss";
        var expectedValue = new SimpleDateFormat(format);
        Assertions.assertEquals(expectedValue.toPattern(),UtilsCommand.getSimpleDateFormat().toPattern());

    }
}