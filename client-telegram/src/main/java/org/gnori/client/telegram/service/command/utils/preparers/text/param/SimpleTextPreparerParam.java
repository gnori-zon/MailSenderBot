package org.gnori.client.telegram.service.command.utils.preparers.text.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SimpleTextPreparerParam implements TextPreparerParam {

    DEFAULT_SUCCESS("✔Success"),
    START_MESSAGE("Select the item you need👇🏿"),
    HELP_MESSAGE("*Choose your mail*"),
    BEFORE_SEND_TO_QUEUE_MESSAGE("Sending to the queue...🛫"),
    AFTER_SEND_TO_QUEUE_MESSAGE("✔Added to queue\n" + START_MESSAGE.getText()),
    SELECT_CHANGE_MESSAGE_ITEM("*Select the item for changing:*"),
    BEFORE_UPLOAD_MESSAGE(
            """
            *Upload the letter in the format .txt" +
            Template: *" +
             ###                    Title                    ###
             ///                 Main content                ///
             ===          Number of emails to each           ===
             :::             Recipients via \",\"            :::
             ---Date of mailing (format: yyyy-MM-dd HH:mm:ss)---
             
             ❕ Data will be skipped if entered incorrectly.
            """
    ),
    BEFORE_CHANGE_TITLE("*Enter a new title: *"),
    BEFORE_CHANGE_TEXT("*Enter new main text: *"),
    BEFORE_CHANGE_SENT_DATE("*Enter a new date of mailing in UTC+3:00 (format: yyyy-MM-dd HH:mm:ss)*"),
    BEFORE_CHANGE_RECIPIENTS("*Enter new recipients : *"),
    BEFORE_CHANGE_COUNT_FOR_RECIPIENT("*Enter the new number of messages per recipient: *"),
    BEFORE_CHANGE_ANNEX("*Enter a new attachment (photo/file): *"),
    BEFORE_CHANGE_MAIL("*Enter new mail: *"),
    BEFORE_CHANGE_KEY_MAIL("*Enter new key for mail: *"),

    AFTER_UPLOAD_MESSAGE_INVALID("❌Submit a file in the format .txt"),
    AFTER_CHANGE_SENT_DATE_INVALID("❌You entered the date in the wrong format, please try again"),
    AFTER_CHANGE_COUNT_FOR_RECIPIENT_INVALID("❌Please enter a number, please try again"),
    AFTER_CHANGE_MAIL_ALREADY_EXIST("❌This mail is already in use, please try again"),
    AFTER_CHANGE_MAIL_INVALID("❌Invalid mail, please try again"),
    AFTER_CHANGE_KEY_MAIL_INVALID("❌Invalid key, please try again"),
    UNDEFINED(
            """
            This command is not implemented👀
            Please use the buttons👌
            """
    );

    private final String text;
}
