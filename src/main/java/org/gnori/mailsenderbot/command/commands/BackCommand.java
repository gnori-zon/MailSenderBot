package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.CommandName.*;
import static org.gnori.mailsenderbot.utils.UrlDatePreparer.prepareUrlsForHelpMessage;
/**
 * Calls the previous command (implementation BACK) {@link Command}.
 */
public class BackCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final MessageTypesDistributorService messageTypesDistributorService;

    public BackCommand(ModifyDataBaseService modifyDataBaseService,
                       MessageTypesDistributorService messageTypesDistributorService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.messageTypesDistributorService = messageTypesDistributorService;
    }

    @Override
    public void execute(Update update) {
        var originalMessage = update.getCallbackQuery().getMessage();
        var firstCallBackDataFromOriginal = originalMessage.getReplyMarkup().getKeyboard().get(0).get(0).getCallbackData();
        var chatId = originalMessage.getChatId();
        var firstUrlText =originalMessage.getReplyMarkup().getKeyboard().get(0).get(0).getText();

        if (CHANGE_MAIL.getCommandName().equals(firstCallBackDataFromOriginal)){
            update.getCallbackQuery().setData(BEGINNING.getCommandName());
            messageTypesDistributorService.distributeMessageByType(update);
        }else if (BACK.getCommandName().equals(firstCallBackDataFromOriginal)){
            var account = modifyDataBaseService.findAccountById(originalMessage.getChatId());
                if(account!=null && State.NOTHING_PENDING.equals(account.getState())){
                    update.getCallbackQuery().setData(BEGINNING.getCommandName());
                    messageTypesDistributorService.distributeMessageByType(update);
            }
                if(account!=null && (State.MAIL_PENDING.equals(account.getState()) ||
                                     State.KEY_FOR_MAIL_PENDING.equals(account.getState()))){
                    update.getCallbackQuery().setData(PROFILE.getCommandName());
                    modifyDataBaseService.updateStateById(chatId,State.NOTHING_PENDING);
                    messageTypesDistributorService.distributeMessageByType(update);
            }
            if(account!=null && (State.TITLE_PENDING.equals(account.getState()) ||
                                 State.CONTENT_PENDING.equals(account.getState()) ||
                                 State.SENT_DATE_PENDING.equals(account.getState()) ||
                                 State.ANNEX_PENDING.equals(account.getState()) ||
                                 State.RECIPIENTS_PENDING.equals(account.getState()) ||
                                 State.DOWNLOAD_MESSAGE_PENDING.equals(account.getState()) ||
                                 State.COUNT_FOR_RECIPIENT_PENDING.equals(account.getState()))){
                update.getCallbackQuery().setData(CREATE_MAILING.getCommandName());
                modifyDataBaseService.updateStateById(chatId,State.NOTHING_PENDING);
                messageTypesDistributorService.distributeMessageByType(update);
            }
        } else if (CLEAR_MESSAGE.getCommandName().equals(firstCallBackDataFromOriginal)){
            update.getCallbackQuery().setData(BEGINNING.getCommandName());
            messageTypesDistributorService.distributeMessageByType(update);
        }else if (CHANGE_ITEM_TITLE.getCommandName().equals(firstCallBackDataFromOriginal) ||
                SEND_ANONYMOUSLY.getCommandName().equals(firstCallBackDataFromOriginal)) {
            update.getCallbackQuery().setData(CREATE_MAILING.getCommandName());
            messageTypesDistributorService.distributeMessageByType(update);
        }else if (prepareUrlsForHelpMessage().get(1).get(0).equals(firstUrlText)){
            update.getCallbackQuery().setData(PROFILE.getCommandName());
            modifyDataBaseService.updateStateById(chatId,State.NOTHING_PENDING);
            messageTypesDistributorService.distributeMessageByType(update);
        }
    }
}
