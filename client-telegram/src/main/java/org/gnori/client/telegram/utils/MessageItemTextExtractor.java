package org.gnori.client.telegram.utils;

import java.util.List;
import java.util.Optional;

public interface MessageItemTextExtractor {

    Optional<String> extractTitle(String from);

    Optional<String> extractText(String from);

    Optional<Integer> extractCountForRecipient(String from);

    List<String> extractRecipients(String from);

    Optional<String> extractSentDate(String from);
}
