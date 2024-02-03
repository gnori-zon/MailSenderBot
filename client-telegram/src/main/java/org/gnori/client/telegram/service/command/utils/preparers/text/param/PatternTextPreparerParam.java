package org.gnori.client.telegram.service.command.utils.preparers.text.param;

import org.gnori.data.dto.AccountDto;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.data.dto.MessageDto;
import org.gnori.data.dto.MessageSentRecordDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record PatternTextPreparerParam(
        String pattern,
        Object... params
) implements TextPreparerParam {

    public static PatternTextPreparerParam profileInfo(AccountDto account) {

        final String mailParam = account.getEmail() != null
                ? account.getEmail()
                : "❌";

        final String isPresentKeyParam = account.isPresentKey() ?
                "✔"
                : "❌";

        return new PatternTextPreparerParam(
                """
                        *Account:*
                         mail: %s
                         mail access key: %s
                        """,
                mailParam, isPresentKeyParam
        );
    }

    public static PatternTextPreparerParam previewMessage(MessageDto message) {

        final String messageTitle = message.title().trim();
        final String messageText = message.text().trim();
        final int lengthTextConstraint = 100;
        final boolean isLengthTextConstraintExceed = messageText.length() > lengthTextConstraint;

        final String titleForResultParam = messageTitle.isEmpty()
                ? "Without title"
                : messageTitle;

        final String textForResultParam = isLengthTextConstraintExceed
                ? "%s...".formatted(messageText.substring(0, 75))
                : messageText;

        final String hasAnnexParam = message.hasAnnex()
                ? "✔"
                : "❌";

        final String recipientsParam = recipientsToString(message.recipients());
        final String countForRecipientParam = String.valueOf(message.countForRecipient());
        final String sentDateParam = message.sentDate();

        return new PatternTextPreparerParam(
                """
                        *Preview:*" +
                                        
                        *%s*
                         %s
                         Attachment: %s
                         Recipients: %s
                         Pieces for each: %s
                         Date of mailing: %s
                        """,
                titleForResultParam, textForResultParam, hasAnnexParam, recipientsParam, countForRecipientParam, sentDateParam
        );
    }

    private static String recipientsToString(List<String> recipients) {

        final int presentLimit = 3;
        final int countRemain = recipients.size() - presentLimit;

        final String suffix = countRemain > 0
                ? "... and %s".formatted(countRemain)
                : "";

        return recipients.stream().limit(presentLimit)
                .collect(Collectors.joining(", ", "", suffix));
    }

    public static PatternTextPreparerParam mailingHistory(MailingHistoryDto mailingHistory) {

        if (mailingHistory == null || mailingHistory.getStateLastMessage() == null) {

            return new PatternTextPreparerParam(
                    """
                            *Mailings history: *
                             You didn't create mailings!
                            """);
        }

        final String stateLastMessageParam = mailingHistory.getStateLastMessage().toString();
        final String mailingListParam = mailingListToString(mailingHistory.getMailingList());

        return new PatternTextPreparerParam(
                """
                        *Mailings history: *
                         Status last mailing: %s
                         Other:
                          %s
                        """,
                stateLastMessageParam, mailingListParam
        );
    }

    private static String mailingListToString(List<MessageSentRecordDto> mailingList) {

        return IntStream.range(1, mailingList.size() + 1)
                .mapToObj(number -> {

                    final MessageSentRecordDto sentRecord = mailingList.get(number - 1);

                    return "%s) %s | %s pcs".formatted(number, sentRecord.getSentDate(), sentRecord.getCountMessages());
                })
                .collect(Collectors.joining("\n  "));
    }
}
