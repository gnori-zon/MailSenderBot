package org.gnori.mailsenderbot.model;

import lombok.*;

import java.util.Collections;
import java.util.List;
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Message {
    private String title;
    private String text;
    private  byte[] annex;
    private List<String> recipients;
    private Integer countForRecipient = 1;

    public String getTitle() {
        return title!=null? title : "";
    }

    public String getText() {
        return text!=null? text : "";
    }

    public byte[] getAnnex() {
        return annex;
    }
    public boolean hasAnnex(){
        return annex!=null;
    }

    public List<String> getRecipients() {
        return recipients!=null? recipients : Collections.emptyList();
    }

    public Integer getCountForRecipient() {
        return countForRecipient;
    }
}
