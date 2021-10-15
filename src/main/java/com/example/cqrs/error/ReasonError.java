package com.example.cqrs.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ReasonError {

    private String traceId;
    private HttpStatus status;
    @Setter
    private String message;


    public ReasonError(String traceId, HttpStatus internalServerError) {
        this(traceId, internalServerError, "Exception not specified");
    }
}
