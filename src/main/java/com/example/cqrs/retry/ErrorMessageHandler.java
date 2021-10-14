package com.example.cqrs.retry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class ErrorMessageHandler {
    private static final String RETRY_COUNT = "count";
    private Dlq dlq;
    private RetrySettings retrySettings;
    private DeadMessageHandlingService deadMessageHandlingService;

    public void handleErroredOutMessage(GenericMessage message, Throwable throwable) {
        MessageHeaders headers = message.getHeaders();
        List<Map> xDeath = (List<Map>) headers.get("x-death");
        Long retryCount = 0L;
        if (xDeath != null) {
            retryCount = (Long) xDeath.get(0).get(RETRY_COUNT);
        }
        if (!retryable(throwable) || maxRetriesAttempted(retryCount)) {
            // max retries attempted.now giving up –don't send to DLQ anymore. Save it first
            deadMessageHandlingService.saveDeadMessage(message, throwable);
            // ImmediateAcknowledgeAmqpException is specific to spring cloud stream
            // when this exception is thrown message is acknowledged and no more sent to dlq or main queus
            throw new ImmediateAcknowledgeAmqpException("Failed message persisted after predefined attempts");
        }
    }


    private boolean retryable(Throwable throwable) {
        if (retrySettings.getSimpleRetryExceptions() != null) {
            for (String exception : retrySettings.getSimpleRetryExceptions()) {
                try {
                    if (Class.forName(exception).isInstance(throwable)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Not able to find class Me");
                }
            }
        }
        return false;
    }

    private boolean maxRetriesAttempted(Long retryCount) {
        return retryCount >= dlq.getMaxRetrials();
    }

    @Getter
    @Setter
    @Configuration
    public static class Dlq {

        @NotNull
        @Value("${spring.cloud.stream.dlq-max-retrials}")
        private Long maxRetrials;
    }
}
