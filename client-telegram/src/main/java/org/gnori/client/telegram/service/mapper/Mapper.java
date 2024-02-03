package org.gnori.client.telegram.service.mapper;

public interface Mapper<T, R> {
    R map(T t);
}
