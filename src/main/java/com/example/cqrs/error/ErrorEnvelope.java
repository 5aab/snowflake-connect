package com.example.cqrs.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ErrorEnvelope<V> {

    private V error;

    private HttpStatus status;

    public static <V> ErrorEnvelope<V> error(V vale) {
        return new ErrorEnvelope<>(Objects.requireNonNull(vale), HttpStatus.CREATED);
    }
}
