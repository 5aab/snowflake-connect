package com.example.cqrs.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class SuccessEnvelope<V> {

    private V data;
    private HttpStatus status;

    public static <V> SuccessEnvelope<V> ok(V value) {
        return new SuccessEnvelope<>(Objects.requireNonNull(value), HttpStatus.OK);
    }
}
