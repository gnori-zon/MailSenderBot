package org.gnori.mailsenderbot.utils.forMail;

public class NoFreeMailingAddressesException extends Exception{
    private static final String BASE_MESSAGE = "Пока все почтовые ящики заняты, попробуйте позже";
    public NoFreeMailingAddressesException(String message){
        super(message);
    }
    public NoFreeMailingAddressesException(){
        super(BASE_MESSAGE);
    }
}
