package org.gnori.mailsenderbot.utils;

import java.util.List;

public class UrlDatePreparer {
    public static List<List<String>> prepareUrlsForHelpMessage(){
        List<String> urls = List.of(
                "https://yandex.ru/support/id/authorization/app-passwords.html",
                "https://support.google.com/accounts/answer/185833?hl=ru",
                "https://help.mail.ru/mail/security/protection/external");
        List<String> urlsText = List.of(
                "Yandex",
                "Gmail",
                "Mail");
        return List.of(urls, urlsText);
    }
}
