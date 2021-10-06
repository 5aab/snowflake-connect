package com.example.cqrs.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class DomainEvent<E extends Enum<E>, T extends DomainAggregateRoot> implements Event {

    private UUID id;
    private String aggregateSource;
    private E type;
    private T entity;
    private LocalDateTime firedOn;

}
