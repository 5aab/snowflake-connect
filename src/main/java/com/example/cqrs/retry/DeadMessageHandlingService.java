package com.example.cqrs.retry;

import lombok.AllArgsConstructor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeadMessageHandlingService {
    public void saveDeadMessage(GenericMessage message, Throwable throwable) {
        //TODO
    }
}
