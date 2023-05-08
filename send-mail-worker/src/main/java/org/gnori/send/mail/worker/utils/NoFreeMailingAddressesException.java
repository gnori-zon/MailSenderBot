package org.gnori.send.mail.worker.utils;

public class NoFreeMailingAddressesException extends Exception{
    private static final String BASE_MESSAGE = "While all mailboxes are busy, please try again later";
    public NoFreeMailingAddressesException(String message){
        super(message);
    }
    public NoFreeMailingAddressesException(){
        super(BASE_MESSAGE);
    }
}
