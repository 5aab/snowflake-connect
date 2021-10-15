package com.example.cqrs.retry.domain;

import com.example.cqrs.event.DomainAggregateRoot;
import com.example.cqrs.event.DomainEvent;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "create")
public class DeadMessage extends DomainAggregateRoot<DomainEvent,UUID> {

    private static final long serialVersionUID = 23342374627456754L;

    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type = "uuid-char")
    @Column(length = 36)
    private UUID id;

    @Lob
    private String errorMessage;

    @Lob
    private String payload;

    @Type(type = "com.example.cqrs.persistence.JsonUserType", parameters = {
            @org.hibernate.annotations.Parameter(name = "classType", value = "java.util.Map"),
            @org.hibernate.annotations.Parameter(name = "viewType", value = "com.example.cqrs.persistence.Views$Headers")
    })
    private Map<String, String> headers;

    private String queueName;

    @Override
    public UUID getIdentity() {
        return id;
    }

    @Override
    public String getAggregateName() {
        return "deadMessage";
    }
}
