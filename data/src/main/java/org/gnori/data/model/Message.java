package org.gnori.data.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record Message(
        long accountId,
        SendMode sendMode,
        String title,
        String text,
        FileData fileData,

        List<String> recipients,
        int countForRecipient,
        LocalDate sentDate

) implements Serializable {

    public boolean hasAnnex() {
        return fileData != null;
    }

    public boolean hasSentDate(){
        return sentDate != null;
    }

    public List<String> recipients() {

        return Optional.ofNullable(recipients)
                .orElseGet(Collections::emptyList);
    }

    public Message withCountForRecipient(Integer countForRecipient) {

        return new Message(
                this.accountId,
                this.sendMode,
                this.title,
                this.text,
                this.fileData,
                this.recipients,
                countForRecipient,
                this.sentDate
        );
    }

    public Message withFileData(FileData fileData) {

        return new Message(
                this.accountId,
                this.sendMode,
                this.title,
                this.text,
                fileData,
                this.recipients,
                this.countForRecipient,
                this.sentDate
        );
    }

    public Message withRecipients(List<String> recipients) {

        return new Message(
                this.accountId,
                this.sendMode,
                this.title,
                this.text,
                this.fileData,
                recipients,
                this.countForRecipient,
                this.sentDate
        );
    }

    public Message withSentDate(LocalDate sentDate) {

        return new Message(
                this.accountId,
                this.sendMode,
                this.title,
                this.text,
                this.fileData,
                this.recipients,
                this.countForRecipient,
                sentDate
        );
    }

    public Message withText(String text) {

        return new Message(
                this.accountId,
                this.sendMode,
                this.title,
                text,
                this.fileData,
                this.recipients,
                this.countForRecipient,
                this.sentDate
        );
    }

    public Message withTitle(String title) {

        return new Message(
                this.accountId,
                this.sendMode,
                title,
                this.text,
                this.fileData,
                this.recipients,
                this.countForRecipient,
                this.sentDate
        );
    }

    public Message with(
            String title,
            String text,
            List<String> recipients,
            Integer countForRecipient,
            LocalDate sentDate
    ) {

        return new Message(
                this.accountId,
                this.sendMode,
                title,
                text,
                this.fileData,
                recipients,
                countForRecipient,
                sentDate
        );
    }

    public Message withSendMode(SendMode sendMode) {

        return new Message(
                this.accountId,
                sendMode,
                this.title,
                this.text,
                this.fileData,
                this.recipients,
                this.countForRecipient,
                this.sentDate
        );
    }
}
