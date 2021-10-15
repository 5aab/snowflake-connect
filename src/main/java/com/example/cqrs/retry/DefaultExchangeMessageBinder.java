package com.example.cqrs.retry;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DefaultExchangeMessageBinder {

    String DEFAULT_EXCHANGE= "defaultExchange";

    @Output
    MessageChannel defaultExchange();
}
