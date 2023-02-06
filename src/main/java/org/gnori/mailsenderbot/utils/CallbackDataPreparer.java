package org.gnori.mailsenderbot.utils;

import org.gnori.mailsenderbot.dto.AccountDto;

import java.util.List;
/**
 * Utils for preparing callback data
 */
public class CallbackDataPreparer {
    public static List<List<String>> prepareCallbackDataForSendMessage(AccountDto account){
        List<String> callbackData = List.of("SEND_ANONYMOUSLY", "SEND_CURRENT_MAIL");
        List<String> callbackDataText = List.of("👽Отправить анонимно", "👁Отпрвить почтой аккаунта");
        if(!(account.getEmail()!=null && account.hasKey())){
            callbackData = List.of("SEND_ANONYMOUSLY");
            callbackDataText = List.of("👽Отправить анонимно");
        }
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForProfileMessage(){
        List<String> callbackData = List.of("CHANGE_MAIL", "CHANGE_KEY", "HELP");
        List<String> callbackDataText = List.of("📮Изменить почту", "🔑Изменить ключ","❔Помощь");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForRegistrationMessage() {
        List<String> callbackData = List.of("BEGINNING");
        List<String> callbackDataText = List.of("🤞Just click button");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForChangeItemMessage() {
        List<String> callbackData = List.of("CHANGE_ITEM_TITLE",
                "CHANGE_ITEM_TEXT",
                "CHANGE_ITEM_ANNEX",
                "CHANGE_ITEM_RECIPIENTS",
                "CHANGE_ITEM_COUNT_FOR_RECIPIENTS",
                "CHANGE_ITEM_SENT_DATE");
        List<String> callbackDataText = List.of(
                "Заголовок",
                "Текст",
                "Приложение",
                "Получатели",
                "Количество шт. каждому",
                "Дата отправки");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForCreateMailingMessage(){
        List<String> callbackData = List.of("CLEAR_MESSAGE","DOWNLOAD_MESSAGE","CHANGE_ITEM","SEND");
        List<String> callbackDataText = List.of("🧼Очистить письмо","📩Загрузить письмо","📝Изменить пункт","✈Отправить");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForBeginningMessage(){
        List<String> callbackData = List.of("MAILING_HISTORY","CREATE_MAILING", "PROFILE");
        List<String> callbackDataText = List.of("📃История рассылок","📧Создать рассылку", "⚙Профиль");
        return List.of(callbackData, callbackDataText);
    }
}
