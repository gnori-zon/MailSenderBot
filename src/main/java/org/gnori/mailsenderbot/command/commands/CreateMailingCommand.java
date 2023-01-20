package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class CreateMailingCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final MessageRepository messageRepository;

    public CreateMailingCommand(SendBotMessageService sendBotMessageService, MessageRepository messageRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForMessage(chatId);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        List<String> callbackData = List.of("CHANGE_ITEM","SEND");
        List<String> callbackDataText = List.of("Изменить пункт","Отправить");
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);
        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }

    private String prepareTextForMessage(Long chatId) {
        var message = messageRepository.getMessage(chatId);
        if(message==null){
            var newMessage = new Message();
            messageRepository.putMessage(chatId, newMessage);
            message = newMessage;
            // for testing
//            newMessage.setTitle(" TITLE ");
//            newMessage.setText(" Подписку оформите а ну что вам сложно чтолим а Москва ээто город и река, да да суперпозиция. И да да счастье в деньгах");
//            byte o = 0;
//            byte i = 1;
//            byte[] annex = {i,o,o,o,o,i,i,o,i,o,i,o,i,o,i,i,o,i,i,i,o};
//            newMessage.setAnnex(annex);
//            newMessage.setCountForRecipient(2);
//            newMessage.setRecipients(List.of("asdasd@bk.ru", "13a@gmail.com", "fff00x@yandex.ru","asdasd@bk.ru", "13a@gmail.com", "fff00x@yandex.ru","asdasd@bk.ru", "13a@gmail.com", "fff00x@yandex.ru"));
        }
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
            resultText = (rawText.substring(0,rawText.length()-2))+"... и еще "+countRemain;

        }
        return resultText;
    }
}