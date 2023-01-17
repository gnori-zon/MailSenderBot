package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
@Service
public class MessageTypesDistributorServiceImpl implements MessageTypesDistributorService {
    private final AccountDao accountDao;
    private final Command command; // TODO удалить и заменить контейнером
    public MessageTypesDistributorServiceImpl(AccountDao accountDao, Command command) {
        this.accountDao = accountDao;
        this.command = command;
    }

    @Override
    public void distributeMessageByType(Update update) {
            var message = update.getMessage();

            if (message.hasText()) {
                processTextMessage(update);
            } else if (message.hasDocument()) {
                processDocMessage(update);
            } else if (message.hasPhoto()) {
                processPhotoMessage(update);
            } else {
                processUnsupportedMessageTypeView(update);
            }

    }

    private void processTextMessage(Update update) {
        var account = accountDao.findById(update.getMessage().getChatId());
        if(account.isEmpty()){
            command.execute(update);
        }
        var commandIdentifier = update.getMessage().getText();
//   TODO implement commandContainer.retrieveCommand(commandIdentifier).execute(update);

    }

    private void processDocMessage(Update update) {

    }

    private void processPhotoMessage(Update update) {

    }

    private void processUnsupportedMessageTypeView(Update update) {

    }
}
