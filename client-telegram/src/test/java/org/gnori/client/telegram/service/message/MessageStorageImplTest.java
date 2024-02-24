package org.gnori.client.telegram.service.message;

import org.gnori.data.model.FileData;
import org.gnori.data.model.FileType;
import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@DisplayName("Unit-level testing for MessageStorageImpl")
class MessageStorageImplTest {

    private final MessageStorage storage = new MessageStorageImpl();

    @Test
    void getMessageShouldReturnDefaultMessage() {

        final long accountId = 1L;
        final Message expected = new Message(
                accountId,
                SendMode.ANONYMOUSLY,
                "",
                "",
                null,
                Collections.emptyList(),
                1,
                null
        );

        final Message actual = storage.getMessage(accountId);

        Assertions.assertEquals(expected.accountId(), actual.accountId());
        Assertions.assertEquals(expected.sendMode(), actual.sendMode());
        Assertions.assertEquals(expected.title(), actual.title());
        Assertions.assertEquals(expected.text(), actual.text());
        Assertions.assertNull(actual.fileData());
        Assertions.assertEquals(expected.recipients(), actual.recipients());
        Assertions.assertEquals(expected.countForRecipient(), actual.countForRecipient());
        Assertions.assertNull(actual.sentDate());
    }

    @Test
    void getMessageShouldReturnCustomMessage() {

        final long accountId = 21L;
        final Message expected = new Message(
                accountId,
                SendMode.CURRENT_MAIL,
                "title",
                "text",
                new FileData("id", "name", FileType.PHOTO),
                List.of("asd", "asd"),
                32,
                LocalDate.now().plusDays(12)
        );

        storage.updateMessage(accountId, expected);
        final Message actual = storage.getMessage(accountId);

        assertMessages(expected, actual);
    }

    @Test
    void updateMessageShouldReplaceMessage() {

        final long accountId = 21L;
        final Message expected = new Message(
                accountId,
                SendMode.CURRENT_MAIL,
                "title",
                "text",
                new FileData("id", "name", FileType.PHOTO),
                List.of("asd", "asd"),
                32,
                LocalDate.now().plusDays(12)
        );

        storage.updateMessage(accountId, expected);
        final Message actual = storage.getMessage(accountId);

        assertMessages(expected, actual);

        final Message secondExpected = new Message(
                accountId,
                SendMode.ANONYMOUSLY,
                "other-title",
                "other-text",
                new FileData("other-id", "other-name", FileType.DOCUMENT),
                List.of("other-asd", "other-asd"),
                1232,
                LocalDate.now().minusYears(1)
        );

        storage.updateMessage(accountId, secondExpected);
        final Message secondActual = storage.getMessage(accountId);

        assertMessages(secondExpected, secondActual);
    }

    @Test
    void clearMessageShouldDeleteCustomMessage() {

        final long accountId = 233L;
        final Message expected = new Message(
                accountId,
                SendMode.CURRENT_MAIL,
                "21-title",
                "tex23t",
                new FileData("33id", "ad_dsd_name", FileType.PHOTO),
                List.of("a13sd", "asd123"),
                3222,
                LocalDate.now()
        );

        storage.updateMessage(accountId, expected);
        final Message actual = storage.getMessage(accountId);

        assertMessages(expected, actual);

        final Message defaultExpected = new Message(
                accountId,
                SendMode.ANONYMOUSLY,
                "",
                "",
                null,
                Collections.emptyList(),
                1,
                null
        );

        storage.clearMessage(accountId);
        final Message secondActual = storage.getMessage(accountId);

        assertMessages(defaultExpected, secondActual);
    }

    private void assertMessages(Message expected, Message actual) {

        Assertions.assertEquals(expected.accountId(), actual.accountId());
        Assertions.assertEquals(expected.sendMode(), actual.sendMode());
        Assertions.assertEquals(expected.title(), actual.title());
        Assertions.assertEquals(expected.text(), actual.text());
        Assertions.assertEquals(expected.fileData(), actual.fileData());
        Assertions.assertEquals(expected.recipients(), actual.recipients());
        Assertions.assertEquals(expected.countForRecipient(), actual.countForRecipient());
        Assertions.assertEquals(expected.sentDate(), actual.sentDate());
    }
}