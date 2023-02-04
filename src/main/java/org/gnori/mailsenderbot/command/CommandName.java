package org.gnori.mailsenderbot.command;

public enum CommandName {
    REGISTRATION("/start"),
    HELP("HELP"),
    BEGINNING("BEGINNING"),
    BACK("BACK"),
    MAILING_HISTORY("MAILING_HISTORY"),
    CREATE_MAILING("CREATE_MAILING"),
    CHANGE_MAIL("CHANGE_MAIL"),
    CHANGE_KEY("CHANGE_KEY"),
    KEY_FOR_MAIL_PENDING("KEY_FOR_MAIL_PENDING"),
    MAIL_PENDING("MAIL_PENDING"),
    CLEAR_MESSAGE("CLEAR_MESSAGE"),
    CHANGE_ITEM("CHANGE_ITEM"),
    CHANGE_ITEM_TITLE("CHANGE_ITEM_TITLE"),
    CHANGE_ITEM_TEXT("CHANGE_ITEM_TEXT"),
    CHANGE_ITEM_ANNEX("CHANGE_ITEM_ANNEX"),
    CHANGE_ITEM_RECIPIENTS("CHANGE_ITEM_RECIPIENTS"),
    CHANGE_ITEM_COUNT_FOR_RECIPIENTS("CHANGE_ITEM_COUNT_FOR_RECIPIENTS"),
    DOWNLOAD_MESSAGE("DOWNLOAD_MESSAGE"),
    DOWNLOAD_MESSAGE_PENDING("DOWNLOAD_MESSAGE_PENDING"),
    TITLE_PENDING("TITLE_PENDING"),
    CONTENT_PENDING("CONTENT_PENDING"),
    ANNEX_PENDING("ANNEX_PENDING"),
    RECIPIENTS_PENDING("RECIPIENTS_PENDING"),
    COUNT_FOR_RECIPIENT_PENDING("COUNT_FOR_RECIPIENT_PENDING"),
    SEND_ANONYMOUSLY("SEND_ANONYMOUSLY"),
    SEND_CURRENT_MAIL("SEND_CURRENT_MAIL"),
    SEND("SEND"),
    SENT_DATE_PENDING("SENT_DATE_PENDING"),
    CHANGE_ITEM_SENT_DATE("CHANGE_ITEM_SENT_DATE"),
    PROFILE("PROFILE");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
    public String getCommandName(){
        return commandName;
    }
}
