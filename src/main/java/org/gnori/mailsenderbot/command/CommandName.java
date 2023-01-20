package org.gnori.mailsenderbot.command;

public enum CommandName {
    REGISTRATION("/start"),
    BEGINNING("BEGINNING"),
    BACK("BACK"),
    MAILING_HISTORY("MAILING_HISTORY"),
    CREATE_MAILING("CREATE_MAILING"),
    CHANGE_MAIL("CHANGE_MAIL"),
    CHANGE_KEY("CHANGE_KEY"),
    KEY_FOR_MAIL_PENDING("KEY_FOR_MAIL_PENDING"),
    MAIL_PENDING("MAIL_PENDING"),
    CHANGE_ITEM("CHANGE_ITEM"),
    CHANGE_ITEM_TITLE("CHANGE_ITEM_TITLE"),
    CHANGE_ITEM_TEXT("CHANGE_ITEM_TEXT"),
    CHANGE_ITEM_ANNEX("CHANGE_ITEM_ANNEX"),
    CHANGE_ITEM_RECIPIENTS("CHANGE_ITEM_RECIPIENTS"),
    CHANGE_ITEM_COUNT_FOR_RECIPIENTS("CHANGE_ITEM_COUNT_FOR_RECIPIENTS"),
    SEND_ANONYMOUSLY("SEND_ANONYMOUSLY"),
    SEND_CURRENT_MAIL("SEND_CURRENT_MAIL"),
    SEND("SEND"),
    PROFILE("PROFILE");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
    public String getCommandName(){
        return commandName;
    }
}