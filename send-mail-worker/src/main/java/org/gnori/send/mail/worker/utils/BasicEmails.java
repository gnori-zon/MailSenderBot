package org.gnori.send.mail.worker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BasicEmails {
    @Value("${base.mails}")
    private String BASE_MAILS_STRING;
    @Value("${base.keys}")
    private String BASE_KEYS_STRING;
    private List<BasicEmailAddress> basicEmailAddresses = new ArrayList<>();

    @PostConstruct
    private void init(){
        List<String> logins = Arrays.stream(BASE_MAILS_STRING.split(",")).map(String::trim).collect(Collectors.toList());
        List<String> passwords = Arrays.stream(BASE_KEYS_STRING.split(",")).map(String::trim).collect(Collectors.toList());
        for(int i = 0; i < logins.size();i++){
            basicEmailAddresses.add(new BasicEmailAddress(logins.get(i),passwords.get(i)));
        }
    }

    public List<BasicEmailAddress> getBasicEmailAddresses() {
        return basicEmailAddresses;
    }
}
