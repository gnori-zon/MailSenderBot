package org.gnori.shared.flow;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<S, F> {

    static <S, F> Result<S, F> success(S s) {
        return Success.of(s);
    }
    static <S, F> Result<S, F> failure(F f) {
        return Failure.of(f);
    }

    interface Folder<T, S, F> {
        T foldSuccess(Success<S, F> success);
        T foldFailure(Failure<S, F> failure);
    }

    <T> T fold(Folder<T, S, F> folder);
    <T> T fold(Function<S, T> foldSuccess, Function<F, T> foldFailure);

    interface Mapper<S, F, NS, NF> {
        NS mapSuccess(Success<S, F> success);
        NF mapFailure(Failure<S, F> failure);
    }

    <NS, NF> Result<NS, NF> map(Mapper<S, F, NS, NF> mapper);
    <NS, NF> Result<NS, NF> map(Function<S, NS> successMutation, Function<F, NF> failureMutation);
    <NS> Result<NS, F> mapSuccess(Function<S, NS> mutation);
    <NF> Result<S, NF> mapFailure(Function<F, NF> mutation);

    interface FlatMapper<S, F, NS, NF> {
        Result<NS, NF> flatMapSuccess(Success<S, F> success);
        Result<NS, NF> flatMapFailure(Failure<S, F> failure);
    }

    <NS, NF> Result<NS, NF> flatMap(FlatMapper<S, F, NS, NF> flatMapper);
    <NS, NF> Result<NS, NF> flatMap(Function<S, Result<NS, NF>> successMutation, Function<F, Result<NS, NF>> failureMutation);
    <NS> Result<NS, F> flatMapSuccess(Function<S, Result<NS, F>> mutation);
    <NF> Result<S, NF> flatMapFailure(Function<F, Result<S, NF>> mutation);


    Result<S,F> doAnyway(Runnable action);
    Result<S,F> doIfSuccess(Consumer<S> action);
    Result<S,F> doIfFailure(Consumer<F> action);

    record Success<S, F>(S value) implements Result<S, F> {

        public static <S, F> Success<S, F> of(S value) {
            return new Success<>(value);
        }

        @Override
        public Result<S, F> doAnyway(Runnable action) {
            action.run();
            return this;
        }

        @Override
        public Result<S, F> doIfSuccess(Consumer<S> action) {

            action.accept(this.value());
            return this;
        }

        @Override
        public Result<S, F> doIfFailure(Consumer<F> action) {
            return this;
        }

        @Override
        public <T> T fold(Folder<T, S, F> folder) {
            return folder.foldSuccess(this);
        }

        @Override
        public <T> T fold(Function<S, T> foldSuccess, Function<F, T> foldFailure) {
            return foldSuccess.apply(this.value());
        }

        @Override
        public <NS, NF> Result<NS, NF> map(Mapper<S, F, NS, NF> mapper) {
            return Result.success(mapper.mapSuccess(this));
        }

        @Override
        public <NS, NF> Result<NS, NF> map(Function<S, NS> successMutation, Function<F, NF> failureMutation) {
            return Result.success(successMutation.apply(this.value()));
        }

        @Override
        public <NS> Result<NS, F> mapSuccess(Function<S, NS> mutation) {
            return Result.success(mutation.apply(this.value));
        }

        @Override
        public <NF> Result<S, NF> mapFailure(Function<F, NF> mutation) {
            return Result.success(this.value);
        }

        @Override
        public <NS, NF> Result<NS, NF> flatMap(FlatMapper<S, F, NS, NF> flatMapper) {
            return flatMapper.flatMapSuccess(this);
        }

        @Override
        public <NS, NF> Result<NS, NF> flatMap(Function<S, Result<NS, NF>> successMutation, Function<F, Result<NS, NF>> failureMutation) {
            return successMutation.apply(this.value());
        }

        @Override
        public <NS> Result<NS, F> flatMapSuccess(Function<S, Result<NS, F>> mutation) {
            return mutation.apply(this.value());
        }

        @Override
        public <NF> Result<S, NF> flatMapFailure(Function<F, Result<S, NF>> mutation) {
            return Result.success(this.value());
        }
    }

    record Failure<S, F>(F value) implements Result<S, F> {

        public static <S, F> Failure<S, F> of(F value) {
            return new Failure<>(value);
        }

        @Override
        public Result<S, F> doAnyway(Runnable action) {
            action.run();
            return this;
        }

        @Override
        public Result<S, F> doIfSuccess(Consumer<S> action) {
            return this;
        }

        @Override
        public Result<S, F> doIfFailure(Consumer<F> action) {
            action.accept(this.value());
            return this;
        }
        @Override
        public <T> T fold(Folder<T, S, F> folder) {
            return folder.foldFailure(this);
        }

        @Override
        public <T> T fold(Function<S, T> foldSuccess, Function<F, T> foldFailure) {
            return foldFailure.apply(this.value());
        }

        @Override
        public <NS, NF> Result<NS, NF> map(Mapper<S, F, NS, NF> mapper) {
            return Result.failure(mapper.mapFailure(this));
        }

        @Override
        public <NS, NF> Result<NS, NF> map(Function<S, NS> successMutation, Function<F, NF> failureMutation) {
            return Result.failure(failureMutation.apply(this.value()));
        }

        @Override
        public <NS> Result<NS, F> mapSuccess(Function<S, NS> mutation) {
            return Result.failure(this.value());
        }

        @Override
        public <NF> Result<S, NF> mapFailure(Function<F, NF> mutation) {
            return Result.failure(mutation.apply(this.value()));
        }

        @Override
        public <NS, NF> Result<NS, NF> flatMap(FlatMapper<S, F, NS, NF> flatMapper) {
            return flatMapper.flatMapFailure(this);
        }

        @Override
        public <NS, NF> Result<NS, NF> flatMap(Function<S, Result<NS, NF>> successMutation, Function<F, Result<NS, NF>> failureMutation) {
            return failureMutation.apply(this.value());
        }

        @Override
        public <NS> Result<NS, F> flatMapSuccess(Function<S, Result<NS, F>> mutation) {
            return Result.failure(this.value());
        }

        @Override
        public <NF> Result<S, NF> flatMapFailure(Function<F, Result<S, NF>> mutation) {
            return mutation.apply(this.value);
        }
    }
}
