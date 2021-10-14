package com.example.cqrs.retry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "create")
public class DeadMessage {

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
}
