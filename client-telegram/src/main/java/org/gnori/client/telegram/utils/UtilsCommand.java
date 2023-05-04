package org.gnori.client.telegram.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import org.gnori.client.telegram.utils.preparers.TextPreparer;

/**
 * Utils for validating data in commands
 */
public class UtilsCommand {
    public static boolean validateMail(String emailAddress) {
        var regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    public static boolean checkedRegistrationCommand(org.telegram.telegrambots.meta.api.objects.Message message) {
        var regMessage = TextPreparer.prepareTextForRegistrationMessage();
        return (message.hasText() && message.getText().contains(regMessage.substring(1,regMessage.length()-1)));
    }
    public static SimpleDateFormat getSimpleDateFormat(){
        var format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format);
    }
}
