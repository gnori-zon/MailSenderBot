package org.gnori.mailsenderbot.utils;

import java.util.regex.Pattern;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForRegistrationMessage;

public class UtilsCommand {
    public static boolean validateMail(String emailAddress) {
        var regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    public static boolean checkedRegistrationCommand(org.telegram.telegrambots.meta.api.objects.Message message) {
        var regMessage = prepareTextForRegistrationMessage();
        return (message.hasText() && message.getText().contains(regMessage.substring(1,regMessage.length()-1)));
    }
}
