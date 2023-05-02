package org.gnori.client.telegram.utils.preparers;

import java.util.List;
import org.gnori.data.dto.AccountDto;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.data.model.Message;

/**
 * Utils for preparing text
 */
public class TextPreparer {
    public static String prepareTextForBeginningMessage() {
        return "Выберите необходимый пункт👇🏿";
    }
    public static String prepareTextForSendCurrentAndAnonymouslyMessage() {
        return "✔Добавлено в очередь\n"+prepareTextForBeginningMessage();
    }
    public static String prepareTextForChangeItemMessage() {
        return "*Выберите пункт, для изменения:*";
    }
    public static String prepareSuccessTextForChangingLastMessage() {
        return "✔Успешно";
    }
    public static String prepareTextForLastForUnknownMessage() {
        return "Данная команда не реализована👀\n" +
                "Пожалуйста, используйте кнопки👌";
    }
    public static String prepareTextForRegistrationMessage() {
        return "*Кликните по кнопке, для начала работы*";
    }
    public static String prepareTextForWaitingForConcreteSendingMessage() {
        return "Производится отправка в очередь...🛫";
    }
    public static String prepareTextForBeforeDownloadMessage() {
        return "*Загрузите письмо в формате .txt"
            + "\n Шаблон: *"+
              "\n\n ###                  Заголовок                  ###"+
                "\n ///                 Текст письма                ///"+
                "\n ===          Количество писем каждому           ==="+
                "\n :::            Получатели через \",\"           :::"+
                "\n ---Дата отправки (в формате yyyy-MM-dd HH:mm:ss)---"+
              "\n\n❕ При некорректном вводе данные будт пропущены";
    }
    public static String prepareTextForAfterSuccessDownloadMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }

    public static String prepareTextForAfterBadDownloadMessage() {
        return "❌Пришлите файл в формате .txt";
    }
    public static String prepareTextForBeforeChangeTitleMessage() {
        return "*Введите новый заголовок: *";
    }
    public static String prepareTextForBeforeChangeSentDateMessage() {
        return "*Введите новую дату отправки по МСК (в формате yyyy-MM-dd HH:mm:ss)*";
    }
    public static String prepareTextInvalidDateForAfterChangeSentDateMessage(){
        return "❌Вы ввели дату в неверном формате, попробуйте снова";
    }
    public static String prepareTextForBeforeChangeRecipientsMessage() {
        return "*Введите новых получателей : *";
    }
    public static String prepareTextForBeforeChangeMailMessage() {
        return "*Введите новый mail: *";
    }
    public static String prepareTextForAfterMailIsExistChangeMailMessage() {
        return "❌Такой mail уже используется, попробуйте снова";
    }

    public static String prepareTextForAfterInvalidMailChangeMailMessage() {
        return "❌Некорректный mail, попробуйте снова";
    }
    public static String prepareTextForBeforeChangeKeyForMailMessage() {
        return "*Введите новый ключ для mail: *";
    }
    public static String prepareTextForAfterEmptyKeyChangeKeyForMailMessage() {
        return "❌Необходимо ввести ключ, попробуйте снова";
    }
    public static String prepareTextForBeforeChangeCountForRecipientsMessage() {
        return  "*Введите новое количество сообщений каждому получателю: *";
    }
    public static String prepareTextForAfterNotNumberChangeCountForRecipientsMessage() {
        return  "❌Необходимо ввести число, попробуйте снова";
    }
    public static String prepareTextForBeforeChangeContentMessage() {
        return "*Введите новый основной текст: *";
    }
    public static String prepareTextForBeforeChangeAnnexMessage() {
        return "*Введите новое приложение (фото/файл): *";
    }

    public static String prepareTextForSendMessage(AccountDto account) {
        var baseText = "*Выберите способ отправки:*";
        if(!(account.getEmail()!=null && account.hasKey())){
            baseText += "\nВы можете добавить свою почту для отправки";
        }
        return baseText;
    }

    public static String prepareTextForProfileMessage(AccountDto account) {
        var email = account.getEmail()!=null ? account.getEmail() : "❌";
        var keyPresent = account.hasKey()? "✔" : "❌";

        return String.format("*Аккаунт:*\n" +
                "почта: %s\n" +
                "ключ доступа к почте: %s",email,keyPresent);
    }
    public static String prepareTextForPreviewMessage(Message message){

        var messageText = message.getText().trim();
        var lengthTextConstraint = messageText.length() > 100;
        var textForResult = lengthTextConstraint ? messageText.substring(0,75) : messageText;
        var suffixResultText = (lengthTextConstraint || messageText.isEmpty())? "...": "";
        var messageTitle= message.getTitle().trim();
        var titleForResult = messageTitle.equals("") ? "Без заголовка" : messageTitle;
        return "*Preview:*" +
                "\n" +
                "\n*"+titleForResult+"*" +
                "\n "+textForResult+suffixResultText +
                "\n Приложение: "+(message.hasAnnex()? "✔" : "❌")+
                "\n Получатели: "+ recipientsToString(message.getRecipients())+
                "\n Шт. каждому: "+ message.getCountForRecipient()+
                "\n Дата отправки: "+(message.hasSentDate()? message.getSentDate():"текущая");
    }

    public static String prepareTextForMessage(MailingHistoryDto mailingHistory) {
        StringBuilder text = new StringBuilder("*История рассылок: *");
        if(mailingHistory!=null) {
            if(mailingHistory.getStateLastMessage()!=null){
                text.append("\nСтатус последней рассылки: ").append(mailingHistory.getStateLastMessage().toString());
            }
            var countLine = 0;
            for(var record : mailingHistory.getMailingList()){
                var line = ++countLine+") "+convertDate(record.getSendDate()) + " | " +record.getCountMessages().toString()+ " шт.";
                text.append("\n").append(line);
            }
            return text.toString();
        }else {
            return text + "\nВы не создавали рассылок!";
        }
    }
    public static String prepareTextForHelpMessage(){
        return "*Выберите свою почту*";
    }

    private static String recipientsToString(List<String> recipients) {
        var resultText = " ";
        var count = 3;
        if(!recipients.isEmpty()) {
            StringBuilder rawText = new StringBuilder();
            recipients.stream().limit(count).forEach(recipient->rawText.append(recipient).append(", "));

            var countRemain = recipients.size() - count;
            var tailForText = countRemain > 0 ? "... и еще "+countRemain : "";
            resultText = (rawText.substring(0,rawText.length()-2))+tailForText;

        }
        return resultText;
    }
    private static String convertDate(String date){
        var pointIndex = date.indexOf(".");
        return date.replace("T"," ").substring(0,pointIndex-3).toLowerCase() + " UTC+3";
    }

}
