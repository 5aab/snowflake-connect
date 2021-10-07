package com.example.cqrs.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DomainEvent<E extends Enum<E>, T extends DomainAggregateRoot> implements Event {

    private UUID id;
    private String aggregateSource;
    private E type;
    private T entity;
    private LocalDateTime firedOn;

    protected DomainEvent(E type, T entity) {
        this.type = type;
        this.entity = entity;
        this.firedOn = LocalDateTime.now();
        this.id = UUID.randomUUID();
        this.aggregateSource = entity.getAggregateName();
    }

    @Override
    public UUID id() {
        return this.id;
    }

}
