package org.gnori.client.telegram.utils.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gnori.shared.flow.Result;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilsCommand {

    private static final String DATE_TIME_FORMAT_PATTER = "yyyy-MM-dd HH:mm:ss";
    private static final String VALID_MAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static Result<String, UtilsCommandFailure> validateMail(String emailAddress) {

        if (emailAddress.matches(VALID_MAIL_REGEX)) {
            return Result.success(emailAddress);
        }

        return Result.failure(UtilsCommandFailure.NOT_VALID_MAIL);
    }

    public static Result<Integer, UtilsCommandFailure> parseNumber(String numberRaw) {

        try {
            return Result.success(Integer.parseInt(numberRaw));
        } catch (NumberFormatException e) {
            return Result.failure(UtilsCommandFailure.NUMBER_FORMAT_EXCEPTION);
        }
    }

    public static Result<LocalDate, UtilsCommandFailure> parseLocalDate(String dateRaw) {

        try {

            final LocalDate parsedDate = LocalDate.parse(dateRaw, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTER));

            return Result.success(parsedDate);
        } catch (DateTimeParseException e) {
            return Result.failure(UtilsCommandFailure.DATE_TIME_PARSE_EXCEPTION);
        }
    }
}
