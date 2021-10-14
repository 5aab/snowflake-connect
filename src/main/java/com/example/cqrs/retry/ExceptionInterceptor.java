package com.example.cqrs.retry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ExceptionInterceptor extends RetryListenerSupport {

    private ErrorMessageHandler errorMessageHandler;

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.error("Retry onError: ", throwable);
        super.onError(context, callback, throwable);
        GenericMessage message = (GenericMessage) context.getAttribute("inputMessage");
        errorMessageHandler.handleErroredOutMessage(message, throwable.getCause());
        throw new TerminatedRetryException(throwable.getMessage());
    }
}