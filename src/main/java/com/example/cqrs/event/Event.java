package com.example.cqrs.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Event {

    UUID id();
    LocalDateTime firedOn();
}
