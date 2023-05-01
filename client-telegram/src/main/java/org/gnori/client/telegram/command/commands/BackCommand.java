package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.command.CommandName.BACK;
import static org.gnori.client.telegram.command.CommandName.BEGINNING;
import static org.gnori.client.telegram.command.CommandName.CHANGE_ITEM_TITLE;
import static org.gnori.client.telegram.command.CommandName.CHANGE_MAIL;
import static org.gnori.client.telegram.command.CommandName.CLEAR_MESSAGE;
import static org.gnori.client.telegram.command.CommandName.CREATE_MAILING;
import static org.gnori.client.telegram.command.CommandName.PROFILE;
import static org.gnori.client.telegram.command.CommandName.SEND_ANONYMOUSLY;
import static org.gnori.client.telegram.utils.preparers.UrlDatePreparer.prepareUrlsForHelpMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.MessageTypesDistributorService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
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
                if(account.isPresent() && State.NOTHING_PENDING.equals(account.get().getState())){
                    update.getCallbackQuery().setData(BEGINNING.getCommandName());
                    messageTypesDistributorService.distributeMessageByType(update);
            }
                if(account.isPresent() && (State.MAIL_PENDING.equals(account.get().getState()) ||
                                     State.KEY_FOR_MAIL_PENDING.equals(account.get().getState()))){
                    update.getCallbackQuery().setData(PROFILE.getCommandName());
                    modifyDataBaseService.updateStateById(chatId,State.NOTHING_PENDING);
                    messageTypesDistributorService.distributeMessageByType(update);
            }
            if(account.isPresent() && (State.TITLE_PENDING.equals(account.get().getState()) ||
                                 State.CONTENT_PENDING.equals(account.get().getState()) ||
                                 State.SENT_DATE_PENDING.equals(account.get().getState()) ||
                                 State.ANNEX_PENDING.equals(account.get().getState()) ||
                                 State.RECIPIENTS_PENDING.equals(account.get().getState()) ||
                                 State.DOWNLOAD_MESSAGE_PENDING.equals(account.get().getState()) ||
                                 State.COUNT_FOR_RECIPIENT_PENDING.equals(account.get().getState()))){
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
