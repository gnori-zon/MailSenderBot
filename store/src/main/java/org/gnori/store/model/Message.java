package org.gnori.store.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Message {
    private Long chatId;
    private SendMode sendMode;
    private String title;
    private String text;
    private Document docAnnex;
    private PhotoSize photoAnnex;
    private byte[] binaryContentForAnnex;

    private List<String> recipients;
    private Integer countForRecipient = 1;
    private Date sentDate;

    public Date getSentDate() {
        return sentDate;
    }
    public SendMode getSendMode() {
        return sendMode;
    }
    public String getTitle() {
        return title!=null? title : "";
    }
    public String getText() {
        return text!=null? text : "";
    }
    public Document getDocAnnex() {
        return docAnnex;
    }
    public PhotoSize getPhotoAnnex() {
        return photoAnnex;
    }
    public boolean hasAnnex(){
        return (docAnnex !=null || photoAnnex !=null);
    }
    public List<String> getRecipients() {
        return recipients!=null? recipients : Collections.emptyList();
    }
    public Integer getCountForRecipient() {
        return countForRecipient;
    }
    public byte[] getBinaryContentForAnnex() {
        return binaryContentForAnnex;
    }
    public Long getChatId() {
        return chatId;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
    public void setSendMode(SendMode sendMode) {
        this.sendMode = sendMode;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setDocAnnex(Document docAnnex) {
        this.docAnnex = docAnnex;
        photoAnnex = null;
        this.binaryContentForAnnex = null;
    }
    public void setPhotoAnnex(PhotoSize photoAnnex) {
        this.photoAnnex = photoAnnex;
        docAnnex = null;
        this.binaryContentForAnnex = null;
    }
    public void setBinaryContentForAnnex(byte[] binaryContentForAnnex) {
        this.binaryContentForAnnex = binaryContentForAnnex;
    }
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
    public void setCountForRecipient(Integer countForRecipient) {
        this.countForRecipient = countForRecipient;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public boolean hasSentDate(){
        return sentDate!=null;
    }

}
