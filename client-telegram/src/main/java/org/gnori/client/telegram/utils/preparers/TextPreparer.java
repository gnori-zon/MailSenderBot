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
        return "Select the item you need👇🏿";
    }
    public static String prepareTextForSendCurrentAndAnonymouslyMessage() {
        return "✔Added to queue\n"+prepareTextForBeginningMessage();
    }
    public static String prepareTextForChangeItemMessage() {
        return "*Select the item for changing:*";
    }
    public static String prepareSuccessTextForChangingLastMessage() {
        return "✔Success";
    }
    public static String prepareTextForLastForUnknownMessage() {
        return "This command is not implemented👀\n" +
                "Please use the buttons👌";
    }
    public static String prepareTextForRegistrationMessage() {
        return "*Click the button to get started*";
    }
    public static String prepareTextForWaitingForConcreteSendingMessage() {
        return "Sending to the queue...🛫";
    }
    public static String prepareTextForBeforeDownloadMessage() {
        return "*Upload the letter in the format .txt" +
            "\n Template: *"+
              "\n\n ###                    Title                    ###"+
                "\n ///                 Main content                ///"+
                "\n ===          Number of emails to each           ==="+
                "\n :::             Recipients via \",\"            :::"+
                "\n ---Date of mailing (format: yyyy-MM-dd HH:mm:ss)---"+
              "\n\n❕ Data will be skipped if entered incorrectly.";
    }
    public static String prepareTextForAfterSuccessDownloadMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }

    public static String prepareTextForAfterBadDownloadMessage() {
        return "❌Submit a file in the format .txt";
    }
    public static String prepareTextForBeforeChangeTitleMessage() {
        return "*Enter a new title: *";
    }
    public static String prepareTextForBeforeChangeSentDateMessage() {
        return "*Enter a new date of mailing in UTC+3:00 (format: yyyy-MM-dd HH:mm:ss)*";
    }
    public static String prepareTextInvalidDateForAfterChangeSentDateMessage(){
        return "❌You entered the date in the wrong format, please try again";
    }
    public static String prepareTextForBeforeChangeRecipientsMessage() {
        return "*Enter new recipients : *";
    }
    public static String prepareTextForBeforeChangeMailMessage() {
        return "*Enter new mail: *";
    }
    public static String prepareTextForAfterMailIsExistChangeMailMessage() {
        return "❌This mail is already in use, please try again";
    }

    public static String prepareTextForAfterInvalidMailChangeMailMessage() {
        return "❌Invalid mail, please try again";
    }
    public static String prepareTextForBeforeChangeKeyForMailMessage() {
        return "*Enter new key for mail: *";
    }
    public static String prepareTextForAfterEmptyKeyChangeKeyForMailMessage() {
        return "❌Key required, please try again";
    }
    public static String prepareTextForBeforeChangeCountForRecipientsMessage() {
        return  "*Enter the new number of messages per recipient: *";
    }
    public static String prepareTextForAfterNotNumberChangeCountForRecipientsMessage() {
        return  "❌Please enter a number, please try again";
    }
    public static String prepareTextForBeforeChangeContentMessage() {
        return "*Enter new main content: *";
    }
    public static String prepareTextForBeforeChangeAnnexMessage() {
        return "*Enter a new attachment (photo/file): *";
    }

    public static String prepareTextForSendMessage(AccountDto account) {
        var baseText = "*Choose a sending method:*";
        if(!(account.getEmail()!=null && account.hasKey())){
            baseText += "\nYou can add your mail to send";
        }
        return baseText;
    }

    public static String prepareTextForProfileMessage(AccountDto account) {
        var email = account.getEmail()!=null ? account.getEmail() : "❌";
        var keyPresent = account.hasKey()? "✔" : "❌";

        return String.format("*Account:*\n" +
                "mail: %s\n" +
                "mail access key: %s", email, keyPresent);
    }
    public static String prepareTextForPreviewMessage(Message message){

        var messageText = message.getText().trim();
        var lengthTextConstraint = messageText.length() > 100;
        var textForResult = lengthTextConstraint ? messageText.substring(0,75) : messageText;
        var suffixResultText = (lengthTextConstraint || messageText.isEmpty())? "...": "";
        var messageTitle= message.getTitle().trim();
        var titleForResult = messageTitle.equals("") ? "Without title" : messageTitle;
        return "*Preview:*" +
                "\n" +
                "\n*"+titleForResult+"*" +
                "\n "+textForResult+suffixResultText +
                "\n Attachment: "+(message.hasAnnex()? "✔" : "❌")+
                "\n Recipients: "+ recipientsToString(message.getRecipients())+
                "\n Pieces for each: "+ message.getCountForRecipient()+
                "\n Date of mailing: "+(message.hasSentDate()? message.getSentDate():"now");
    }

    public static String prepareTextForMessage(MailingHistoryDto mailingHistory) {
        StringBuilder text = new StringBuilder("*Mailings history: *");
        if(mailingHistory!=null) {
            if(mailingHistory.getStateLastMessage()!=null){
                text.append("\nStatus last mailing: ").append(mailingHistory.getStateLastMessage().toString());
            } else {
                return text + "\nYou didn't create mailings!";
            }
            var countLine = 0;
            for(var record : mailingHistory.getMailingList()){
                var line = ++countLine+") "+convertDate(record.getSendDate()) + " | " +record.getCountMessages().toString()+ " pcs";
                text.append("\n").append(line);
            }
            return text.toString();
        }else {
            return text + "\nYou didn't create mailings!";
        }
    }
    public static String prepareTextForHelpMessage(){
        return "*Choose your mail*";
    }

    private static String recipientsToString(List<String> recipients) {
        var resultText = " ";
        var count = 3;
        if(!recipients.isEmpty()) {
            StringBuilder rawText = new StringBuilder();
            recipients.stream().limit(count).forEach(recipient->rawText.append(recipient).append(", "));

            var countRemain = recipients.size() - count;
            var tailForText = countRemain > 0 ? "... and "+countRemain : "";
            resultText = (rawText.substring(0,rawText.length()-2))+tailForText;

        }
        return resultText;
    }
    private static String convertDate(String date){
        var pointIndex = date.indexOf(".");
        return date.replace("T"," ").substring(0,pointIndex-3).toLowerCase() + " UTC+3";
    }

}
