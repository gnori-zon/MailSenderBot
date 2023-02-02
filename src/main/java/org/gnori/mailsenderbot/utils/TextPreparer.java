package org.gnori.mailsenderbot.utils;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.model.Message;

import java.util.List;

public class TextPreparer {
    public static String prepareTextForBeginningMessage() {
        return "Выберите необходимый пункт👇🏿";
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
    public static String prepareTextForSuccessConcreteSendingMessage() {
        return "отправлено✔";
    }
    public static String prepareTextForBadConcreteSendingMessage() {
        return "неотправлено:❌";
    }
    public static String prepareTextForWaitingForConcreteSendingMessage() {
        return "Производится отправка...🛫";
    }
    public static String prepareTextForBeforeDownloadMessage() {
        return "*Загрузите письмо в формате .txt\nШаблон: *"+
                "\n\n ###Заголовок###"+
                "\n ///Текст письма///"+
                "\n ===Количество писем каждому==="+
                "\n :::Получатели через \",\":::";
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
                "\n Шт. каждому: "+ message.getCountForRecipient();
    }

    public static String prepareTextForMessage(MailingHistoryDto mailingHistory) {
        StringBuilder text = new StringBuilder("*История рассылок: *");
        if(mailingHistory!=null) {
            var countLine = 0;
            for(var record : mailingHistory.getMailingList()){
                var line = ++countLine+") "+convertDate(record.getSendDate()) + " | " +record.getCountMessages().toString()+ " шт. каждому получателю ";
                text.append("\n").append(line);
            }
            return text.toString();
        }else {
            return text + "\nВы не создавали рассылок!";
        }
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
    private static String convertDate(String date){
        var pointIndex = date.indexOf(".");
        return date.replace("T"," ").substring(0,pointIndex-3) + " UTC+3";
    }

}
