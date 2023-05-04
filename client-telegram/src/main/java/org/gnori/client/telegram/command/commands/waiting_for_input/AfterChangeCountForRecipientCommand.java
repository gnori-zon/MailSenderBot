package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterNotNumberChangeCountForRecipientsMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Validates and sets the count of recipient in the model and sets the state {@link Command,State}.
 */
public class AfterChangeCountForRecipientCommand implements Command {
        private final ModifyDataBaseService modifyDataBaseService;
        private final SendBotMessageService sendBotMessageService;
        private final MessageRepositoryService messageRepository;

        public AfterChangeCountForRecipientCommand(ModifyDataBaseService modifyDataBaseService,
                                         SendBotMessageService sendBotMessageService,
                                         MessageRepositoryService messageRepository) {
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
