package org.gnori.mailsenderbot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-level testing for FileDataParser")
class FileDataParserTest {

    @Test
    void getTitleFromContentTest() {
        var expectedValue = "This is any words";
        var actualValue = FileDataParser.getTitleFromContent("###"+expectedValue+"###");
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void getCountForRecipientFromContentPositiveTest() {
        var expectedValue = 1243;
        var actualValue = FileDataParser.getCountForRecipientFromContent("==="+expectedValue+"===");
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void getCountForRecipientFromContentNegativeTest() {
        var expectedValue = "This is any words";
        var actualValue = FileDataParser.getCountForRecipientFromContent("==="+expectedValue+"===");
        Assertions.assertNull(actualValue);
    }

    @Test
    void getRecipientsFromContentTest() {
        var expectedValue = List.of("asd@mail.ru","as123@@gmail.com","alsdmpas@yandex.ru");
        var rawValue = expectedValue.toString().substring(1,expectedValue.toString().length()-1);
        var actualValue = FileDataParser.getRecipientsFromContent(":::"+rawValue+":::");
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void getTextFromContentTest() {
        var expectedValue = "This is any words\n sdf sdf \r sdf sdf ";
        var actualValue = FileDataParser.getTextFromContent("///"+expectedValue+"///");
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void getSentDateFromContentTest() {
        var expectedValue = "12.02.2020";
        var actualValue = FileDataParser.getSentDateFromContent("---"+expectedValue+"---");
        Assertions.assertEquals(expectedValue, actualValue);
    }
}