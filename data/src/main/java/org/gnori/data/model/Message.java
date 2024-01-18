package org.gnori.data.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public record Message(
        long chatId,
        SendMode sendMode,
        String title,
        String text,
        FileData fileData,

        List<String> recipients,
        int countForRecipient,
        Date sentDate

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
}
