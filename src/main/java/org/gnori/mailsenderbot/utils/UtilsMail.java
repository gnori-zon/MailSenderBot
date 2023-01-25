package org.gnori.mailsenderbot.utils;

import java.util.Properties;

public class UtilsMail {
    public static Properties getBaseProperties(){
        return getGmailProperties();
    }
    public static Properties getGmailProperties(){
        var props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.protocol","smtp");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.debug","true");
        return props;
    }

}
