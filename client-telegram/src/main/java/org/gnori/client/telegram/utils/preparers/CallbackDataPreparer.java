package org.gnori.client.telegram.utils.preparers;

import java.util.List;
import org.gnori.data.dto.AccountDto;

/**
 * Utils for preparing callback data
 */
public class CallbackDataPreparer {
    public static List<List<String>> prepareCallbackDataForSendMessage(AccountDto account){
        List<String> callbackData = List.of("SEND_ANONYMOUSLY", "SEND_CURRENT_MAIL");
        List<String> callbackDataText = List.of("👽Send anonymously", "👁Send with your mail");
        if(!(account.getEmail()!=null && account.hasKey())){
            callbackData = List.of("SEND_ANONYMOUSLY");
            callbackDataText = List.of("👽Send anonymously");
        }
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForProfileMessage(){
        List<String> callbackData = List.of("CHANGE_MAIL", "CHANGE_KEY", "HELP");
        List<String> callbackDataText = List.of("📮Change mail", "🔑Change key","❔Help");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForRegistrationMessage() {
        List<String> callbackData = List.of("BEGINNING");
        List<String> callbackDataText = List.of("🤞click");
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
                "Title",
                "Content",
                "Attachment",
                "Recipients",
                "Number of pieces for each",
                "Date of mailing");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForCreateMailingMessage(){
        List<String> callbackData = List.of("CLEAR_MESSAGE","DOWNLOAD_MESSAGE","CHANGE_ITEM","SEND");
        List<String> callbackDataText = List.of("🧼Clear message","📩Upload message","📝Change item","✈Send");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForBeginningMessage(){
        List<String> callbackData = List.of("MAILING_HISTORY","CREATE_MAILING", "PROFILE");
        List<String> callbackDataText = List.of("📃Mailings history","📧Create mailing", "⚙Profile");
        return List.of(callbackData, callbackDataText);
    }
}
