package org.gnori.mailsenderbot.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * Authenticator to connect to mail
 */
public class LoginAuthenticator extends Authenticator {
    PasswordAuthentication authentication = null;

    public LoginAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username,password);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
