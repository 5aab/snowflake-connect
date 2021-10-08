package com.example.cqrs.event.relay;

import com.example.cqrs.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MessageRelayHandler {

    public static final String RELAY_DESTINATION = "relay-destination";
    public static final String EVENT_TYPE = "event-type";
    public static final String SOURCE = "event-source";
    public MessageChannel relayMessageChannel;

    public MessageRelayHandler(MessageChannel relayMessageChannel) {
        Assert.notNull(relayMessageChannel, "Aggregate Channel can't be null");
        this.relayMessageChannel = relayMessageChannel;
    }

    @EventListener
    public void relayAggregateEvent(DomainEvent message) {
        String destinationChannel = message.getAggregateSource() + "NotificationChannel";
        log.info("->[DE]-> Relay to {} : {}", destinationChannel, message);
        Map<String, Object> headers = new HashMap<>();
        headers.put(RELAY_DESTINATION, destinationChannel);
        headers.put(EVENT_TYPE, message.getType().name());
        headers.put(SOURCE, message.getAggregateSource());
        relayMessageChannel.send(MessageBuilder.createMessage(message, new MessageHeaders(headers)));
    }

}
