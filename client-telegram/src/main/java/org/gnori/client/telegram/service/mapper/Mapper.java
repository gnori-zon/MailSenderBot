package org.gnori.client.telegram.service.mapper;

@FunctionalInterface
public interface Mapper<T, R> {
    R map(T t);
}
