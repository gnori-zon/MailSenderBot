package org.gnori.data.flow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

@DisplayName("Unit-level testing for Result")
class ResultTest {

    @Test
    void successShouldReturnSuccessClassWithPassedValue() {

        final int value = 12;
        final Result<Integer, Object> successWithInteger = Result.success(value);

        Assertions.assertEquals(Result.Success.class, successWithInteger.getClass());
        Assertions.assertEquals(value, ((Result.Success<?, ?>) successWithInteger).value());
    }

    @Test
    void failureShouldReturnFailureClassWithPassedValue() {

        final int value = 12;
        final Result<Object, Integer> failureWithInteger = Result.failure(value);

        Assertions.assertEquals(Result.Failure.class, failureWithInteger.getClass());
        Assertions.assertEquals(value, ((Result.Failure<?, ?>) failureWithInteger).value());
    }

    @Test
    void successFoldByFolder() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final String result = Result.<String, String>success(value)
                .fold(defaultFolderSuccessToUpperFailureToLower());

        Assertions.assertEquals(expectedValue, result);
    }

    @Test
    void failureFoldByFolder() {

        final String expectedValue = "value-mock";
        final String value = "VaLuE-mOcK";

        final String result = Result.<String, String>failure(value)
                .fold(defaultFolderSuccessToUpperFailureToLower());

        Assertions.assertEquals(expectedValue, result);
    }

    private Result.Folder<String, String, String> defaultFolderSuccessToUpperFailureToLower() {

        return new Result.Folder<>() {

            @Override
            public String foldSuccess(Result.Success<String, String> success) {
                return success.value().toUpperCase();
            }

            @Override
            public String foldFailure(Result.Failure<String, String> failure) {
                return failure.value().toLowerCase();
            }
        };
    }

    @Test
    void successFoldByLambda() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final String result = Result.<String, String>success(value)
                .fold(String::toUpperCase, String::toLowerCase);

        Assertions.assertEquals(expectedValue, result);
    }

    @Test
    void failureFoldByLambda() {

        final String expectedValue = "value-mock";
        final String value = "VaLuE-mOcK";

        final String result = Result.<String, String>failure(value)
                .fold(String::toUpperCase, String::toLowerCase);

        Assertions.assertEquals(expectedValue, result);
    }

    @Test
    void successMapByMapper() {

        final String value = "VaLuE-mOcK";
        final String expectedValue = "VALUE-MOCK";

        final Result<String, Integer> result = Result.<String, String>success(value)
                .map(defaultMapperSuccessToUpperFailureToCountCharacters());

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void failureMapByMapper() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .map(defaultMapperSuccessToUpperFailureToCountCharacters());

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    private Result.Mapper<String, String, String, Integer> defaultMapperSuccessToUpperFailureToCountCharacters() {

        return new Result.Mapper<>() {
            @Override
            public String mapSuccess(Result.Success<String, String> success) {
                return success.value().toUpperCase();
            }

            @Override
            public Integer mapFailure(Result.Failure<String, String> failure) {
                return failure.value().length();
            }
        };
    }

    @Test
    void successMapByLamda() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, Integer> result = Result.<String, String>success(value)
                .map(String::toUpperCase, String::length);

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void failureMapByLamda() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .map(String::toUpperCase, String::length);

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    @Test
    void mapSuccess() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, String> result = Result.<String, String>success(value)
                .mapSuccess(String::toUpperCase);

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void mapFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .mapFailure(String::length);

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    @Test
    void successFlatMapByFlatMapperToSuccess() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, Integer> result = Result.<String, String>success(value)
                .flatMap(defaultFlatMapperSuccessToSuccessWithUpperCaseAndFailureToFailureWithCountCharacters());

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }


    @Test
    void failureFlatMapByFlatMapperToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .flatMap(defaultFlatMapperSuccessToSuccessWithUpperCaseAndFailureToFailureWithCountCharacters());

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    @Test
    void successFlatMapByFlatMapperToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>success(value)
                .flatMap(defaultFlatMapperSuccessToFailureWithCountCharactersAndFailureToSuccessWithUpperCase());

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }


    @Test
    void failureFlatMapByFlatMapperToSuccess() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .flatMap(defaultFlatMapperSuccessToFailureWithCountCharactersAndFailureToSuccessWithUpperCase());

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    private Result.FlatMapper<String, String, String, Integer> defaultFlatMapperSuccessToSuccessWithUpperCaseAndFailureToFailureWithCountCharacters() {

        return new Result.FlatMapper<>() {
            @Override
            public Result<String, Integer> flatMapSuccess(Result.Success<String, String> success) {
                return Result.success(success.value().toUpperCase());
            }

            @Override
            public Result<String, Integer> flatMapFailure(Result.Failure<String, String> failure) {
                return Result.failure(failure.value().length());
            }
        };
    }

    private Result.FlatMapper<String, String, String, Integer> defaultFlatMapperSuccessToFailureWithCountCharactersAndFailureToSuccessWithUpperCase() {

        return new Result.FlatMapper<>() {
            @Override
            public Result<String, Integer> flatMapSuccess(Result.Success<String, String> success) {
                return Result.failure(success.value().length());
            }

            @Override
            public Result<String, Integer> flatMapFailure(Result.Failure<String, String> failure) {
                return Result.success(failure.value().toUpperCase());
            }
        };
    }

    @Test
    void successFlatMapByLambdaToSuccess() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, Integer> result = Result.<String, String>success(value)
                .flatMap(
                        successValue -> Result.success(successValue.toUpperCase()),
                        failureValue -> Result.failure(failureValue.length())
                );

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }


    @Test
    void failureFlatMapByLambdaToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .flatMap(
                        successValue -> Result.success(successValue.toUpperCase()),
                        failureValue -> Result.failure(failureValue.length())
                );

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }


    @Test
    void successFlatMapByLambdaToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>success(value)
                .flatMap(
                        successValue -> Result.failure(successValue.length()),
                        failureValue -> Result.success(failureValue.toUpperCase())
                );

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }


    @Test
    void failureFlatMapByLambdaToSuccess() {

        final String expectedValue = "VALUE-MOCK";
        final String value = "VaLuE-mOcK";

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .flatMap(
                        successValue -> Result.failure(successValue.length()),
                        failureValue -> Result.success(failureValue.toUpperCase())
                );

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void flatMapSuccessToSuccess() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<Integer, String> result = Result.<String, String>success(value)
                .flatMapSuccess(successValue -> Result.success(successValue.length()));

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void flatMapSuccessToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, Integer>success(value)
                .flatMapSuccess(successValue -> Result.failure(successValue.length()));

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    @Test
    void flatMapFailureToFailure() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<String, Integer> result = Result.<String, String>failure(value)
                .flatMapFailure(failureValue -> Result.failure(failureValue.length()));

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Failure<?, ?>) result).value());
    }

    @Test
    void flatMapFailureToSuccess() {

        final String value = "VaLuE-mOcK";
        final int expectedValue = value.length();

        final Result<Integer, String> result = Result.<Integer, String>failure(value)
                .flatMapFailure(failureValue -> Result.success(failureValue.length()));

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertEquals(expectedValue, ((Result.Success<?, ?>) result).value());
    }

    @Test
    void doAnywayWhenSuccess() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Integer, Object> result = Result.success(12)
                .doAnyway(() -> isInvoked.set(true));

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertTrue(isInvoked.get());
    }

    @Test
    void doAnywayWhenFailure() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Object, Integer> result = Result.failure(12)
                .doAnyway(() -> isInvoked.set(true));

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertTrue(isInvoked.get());
    }

    @Test
    void doIfSuccessShouldInvokedWhenSuccess() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Integer, Object> result = Result.success(12)
                .doIfSuccess(success -> isInvoked.set(true));

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertTrue(isInvoked.get());
    }

    @Test
    void doIfSuccessShouldNotInvokedWhenFailure() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Object, Integer> result = Result.failure(12)
                .doIfSuccess(success -> isInvoked.set(true));

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertFalse(isInvoked.get());
    }

    @Test
    void doIfFailureShouldInvokedWhenFailure() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Object, Integer> result = Result.failure(12)
                .doIfFailure(failure -> isInvoked.set(true));

        Assertions.assertEquals(Result.Failure.class, result.getClass());
        Assertions.assertTrue(isInvoked.get());
    }

    @Test
    void doIfFailureShouldInvokedWhenSuccess() {

        final AtomicBoolean isInvoked = new AtomicBoolean();
        final Result<Integer, Object> result = Result.success(12)
                .doIfFailure(failure -> isInvoked.set(true));

        Assertions.assertEquals(Result.Success.class, result.getClass());
        Assertions.assertFalse(isInvoked.get());
    }
}