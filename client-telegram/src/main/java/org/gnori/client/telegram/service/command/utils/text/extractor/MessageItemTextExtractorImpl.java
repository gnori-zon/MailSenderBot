package org.gnori.client.telegram.service.command.utils.text.extractor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageItemTextExtractorImpl implements MessageItemTextExtractor {

    private static final String TITLE_PATTERN = "###(.*)###";
    private static final String TEXT_PATTERN = "///((.*|\\n|\\r)+)///";
    private static final String COUNT_FOR_RECIPIENT_PATTERN = "===(\\d+)===";
    private static final String RECIPIENTS_PATTERN = ":::((.*|\\n|\\r)+):::";

    private static final String SENT_DATE_PATTERN = "---(.*)---";


    @Override
    public Optional<String> extractTitle(String content) {
        return extractMatch(content, TITLE_PATTERN);
    }

    @Override
    public Optional<String> extractText(String content) {
        return extractMatch(content, TEXT_PATTERN);
    }

    @Override
    public Optional<Integer> extractCountForRecipient(String content) {

        return extractMatch(content, COUNT_FOR_RECIPIENT_PATTERN)
                .map(Integer::valueOf);
    }

    @Override
    public List<String> extractRecipients(String content) {

        return extractMatch(content, RECIPIENTS_PATTERN)
                .map(recipientListRaw -> recipientListRaw.split(","))
                .stream()
                .flatMap(Arrays::stream)
                .map(String::trim)
                .distinct()
                .toList();
    }

    @Override
    public Optional<String> extractSentDate(String content) {
        return extractMatch(content, SENT_DATE_PATTERN);
    }

    private Optional<String> extractMatch(String from, String regexPattern) {

        final Pattern pattern = Pattern.compile(regexPattern);
        final Matcher matcher = pattern.matcher(from);

        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }
}
