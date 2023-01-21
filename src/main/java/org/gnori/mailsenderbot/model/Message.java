package org.gnori.mailsenderbot.model;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Collections;
import java.util.List;
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Message {
    private String title;
    private String text;
    private Document DocAnnex;
    private PhotoSize PhotoAnnex;
    private List<String> recipients;
    private Integer countForRecipient = 1;

    public String getTitle() {
        return title!=null? title : "";
    }

    public String getText() {
        return text!=null? text : "";
    }

    public Document getDocAnnex() {
        return DocAnnex;
    }

    public PhotoSize getPhotoAnnex() {
        return PhotoAnnex;
    }

    public boolean hasAnnex(){
        return (DocAnnex!=null || PhotoAnnex!=null);
    }

    public List<String> getRecipients() {
        return recipients!=null? recipients : Collections.emptyList();
    }

    public Integer getCountForRecipient() {
        return countForRecipient;
    }
}
