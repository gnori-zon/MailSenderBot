package org.gnori.send.mail.worker.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.data.util.StreamUtils;

@ConstructorBinding
@ConfigurationProperties("base-mails-data")
public final class DefaultEmailDataImpl implements DefaultEmailData {

    private static final String SPLIT_REGEX = ",";

    private final ConcurrentLinkedQueue<EmailData> emailData;

    public DefaultEmailDataImpl(
            String mails,
            String keys
    ) {

        emailData = StreamUtils.zip(
                        split(mails, SPLIT_REGEX).map(String::trim),
                        split(keys, SPLIT_REGEX).map(String::trim),
                        EmailData::new
                )
                .collect(
                        ConcurrentLinkedQueue::new,
                        ConcurrentLinkedQueue::add,
                        ConcurrentLinkedQueue::addAll
                );
    }

    @Override
    public Optional<EmailData> find() {
        return Optional.ofNullable(emailData.poll());
    }

    @Override
    public void add(EmailData email) {

        Optional.ofNullable(email)
                .ifPresent(emailData::add);
    }

    private Stream<String> split(String raw, String regex) {
        return Arrays.stream(raw.split(regex));
    }
}
