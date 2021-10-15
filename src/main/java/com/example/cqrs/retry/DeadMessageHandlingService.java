package com.example.cqrs.retry;

import com.example.cqrs.retry.domain.DeadMessage;
import com.example.cqrs.retry.domain.DeadMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@AllArgsConstructor
@EnableBinding(DefaultExchangeMessageBinder.class)
public class DeadMessageHandlingService {
    private DefaultExchangeMessageBinder defaultExchangeMessageBinder;
    private DeadMessageRepository deadMessageRepository;

    public void saveDeadMessage(GenericMessage message, Throwable throwable) {
        try {
            log.error("Message is dead. save it");
            MessageHeaders messageHeaders = message.getHeaders();
            DeadMessage deadMessage = DeadMessage.builder()
                    .payload(new String((byte[]) message.getPayload(), StandardCharsets.UTF_8))
                    .errorMessage(throwable != null ? throwable.getMessage() : "No Error Definition Found")
                    .headers(extractHeaders(messageHeaders))
                    .queueName((String) messageHeaders.get("amqp_consumerQueue"))
                    .build();
            deadMessageRepository.save(deadMessage);
        } catch (Exception e) {
            log.error("Error occurred while saving dead message", e);
        }
    }

    private Map<String, String> extractHeaders(MessageHeaders messageHeaders) {
        return messageHeaders.entrySet().stream().collect(toMap(e -> e.getKey(), e -> String.valueOf(e.getValue())));
    }

    @Transactional
    public List<GenericMessage> fetchMessages(List<UUID> ids) {
        List<DeadMessage> distributionErrors = deadMessageRepository.findAllById(ids);
        return distributionErrors.stream().map(this::toGenericMessage).collect(Collectors.toList());
    }

    private GenericMessage toGenericMessage(DeadMessage deadMessage) {
        return new GenericMessage(deadMessage.getPayload(), getMessageHeaders(deadMessage));
    }

    private Map<String, Object> getMessageHeaders(DeadMessage deadMessage) {
        Map<String, Object> headers = deadMessage.getHeaders().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        headers.put("key", deadMessage.getQueueName());
        return headers;
    }

    public void replay(List<UUID> ids) {
        List<GenericMessage> messagedToBePlayed = fetchMessages(ids);
        log.info("Total Messages Fetched {}", messagedToBePlayed.size());
        for (GenericMessage message : messagedToBePlayed) {
            MessageHeaders headers = message.getHeaders();
            log.info("Going to post to queue {}", headers.get("amqp_consumerQueue"));
            defaultExchangeMessageBinder.defaultExchange().send(MessageBuilder.withPayload(message.getPayload()).copyHeaders(headers).build());
        }
        deleteDeadMessages(ids);
    }

    private void deleteDeadMessages(List<UUID> ids) {
        ids.forEach(deadMessageRepository::deleteById);
    }
}
