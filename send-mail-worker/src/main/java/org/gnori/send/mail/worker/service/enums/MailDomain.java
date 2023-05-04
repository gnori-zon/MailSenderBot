package org.gnori.send.mail.worker.service.enums;

import java.util.List;

public enum MailDomain {
    GMAIL(List.of("gmail")),
    YANDEX(List.of("ya","yandex")),
    MAIL(List.of("mail","inbox","bk","list", "internet"));
    private final List<String> domainList;

    MailDomain(List<String> domainList) {
        this.domainList = domainList;
    }

    public List<String> getDomainList(){
        return domainList;
    }

}
