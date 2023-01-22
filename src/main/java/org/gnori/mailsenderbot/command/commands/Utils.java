package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;

import java.util.List;

public class Utils {
    public static String prepareTextForProfileMessage(AccountDto account) {
        var email = account.getEmail()!=null ? account.getEmail() : "❌";
        var keyPresent = account.hasKey()? "✔" : "❌";

        return String.format("*Аккаунт:*\n" +
                "почта: %s\n" +
                "ключ доступа к почте: %s",email,keyPresent);
    }
    public static List<List<String>> prepareCallbackDataForProfileMessage(){
        List<String> callbackData = List.of("CHANGE_MAIL", "CHANGE_KEY");
        List<String> callbackDataText = List.of("Изменить почту", "Изменить ключ");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForCreateMailingMessage(){
        List<String> callbackData = List.of("CHANGE_ITEM","SEND");
        List<String> callbackDataText = List.of("Изменить пункт","Отправить");
        return List.of(callbackData, callbackDataText);
    }
    public static List<List<String>> prepareCallbackDataForBeginningMessage(){
        List<String> callbackData = List.of("MAILING_HISTORY","CREATE_MAILING", "PROFILE");
        List<String> callbackDataText = List.of("📃История рассылок","📧Создать рассылку", "⚙Профиль");
        return List.of(callbackData, callbackDataText);
    }
    public static String prepareTextForPreviewMessage(Message message){

        var messageText = message.getText().trim();
        var textForResult = messageText.length() > 100 ? messageText.substring(0,75) : messageText;
        var messageTitle= message.getTitle().trim();
        var titleForResult = messageTitle.equals("") ? "Без заголовка" : messageTitle;
        return "*Preview:*" +
                "\n" +
                "\n*"+titleForResult+"*" +
                "\n "+textForResult+"..." +
                "\n Приложение: "+(message.hasAnnex()? "✔" : "❌")+
                "\n Получатели: "+ recipientsToString(message.getRecipients())+
                "\n Шт. каждому: "+ message.getCountForRecipient();
    }

    private static String recipientsToString(List<String> recipients) {
        var resultText = " ";
        var count = 3;
        if(!recipients.isEmpty()) {
            StringBuilder rawText = new StringBuilder();
            recipients.stream().limit(count).forEach(recipient->{rawText.append(recipient).append(", ");});

            var countRemain = recipients.size() - count;
            var tailForText = countRemain > 0 ? "... и еще "+countRemain : "";
            resultText = (rawText.substring(0,rawText.length()-2))+tailForText;

        }
        return resultText;
    }
    public static String prepareTextForMessage(MailingHistoryDto mailingHistory) {
        StringBuilder text = new StringBuilder("*История рассылок: *");
        if(mailingHistory!=null) {
            var countLine = 0;
            for(var record : mailingHistory.getMailingList()){
                var line = ++countLine+") "+convertDate(record.getSendDate()) + " | " +record.getCountMessages().toString()+ " шт. каждому получателю ";
                text.append("\n").append(line);
            }
        }
        return text+"\nВы не создавали рассылок!";
    }
    private static String convertDate(String date){
        var pointIndex = date.indexOf(".");
        return date.replace("T"," ").substring(0,pointIndex-3) + " UTC+3";
    }
}
