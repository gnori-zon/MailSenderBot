package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.*;
/**
 * Validates and sets the count of recipient in the model and sets the state {@link Command,State}.
 */
public class AfterChangeCountForRecipientCommand implements Command {
        private final ModifyDataBaseService modifyDataBaseService;
        private final SendBotMessageService sendBotMessageService;
        private final MessageRepository messageRepository;

        public AfterChangeCountForRecipientCommand(ModifyDataBaseService modifyDataBaseService,
                                         SendBotMessageService sendBotMessageService,
                                         MessageRepository messageRepository) {
            this.modifyDataBaseService = modifyDataBaseService;
            this.sendBotMessageService = sendBotMessageService;
            this.messageRepository = messageRepository;
        }

        @Override
        public void execute(Update update) {

            var newCount = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            var textForOld = "";

            var message = messageRepository.getMessage(chatId);
            try {
                message.setCountForRecipient(Integer.parseInt(newCount));
                messageRepository.putMessage(chatId,message);
                textForOld = prepareSuccessTextForChangingLastMessage();
            }catch (NumberFormatException e){
                textForOld = prepareTextForAfterNotNumberChangeCountForRecipientsMessage();
            }

            var lastMessageId = update.getMessage().getMessageId() - 1;
            var text = prepareTextForPreviewMessage(message);
            var newCallbackData = prepareCallbackDataForCreateMailingMessage();

            modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);
            sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
            sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);
        }
}
