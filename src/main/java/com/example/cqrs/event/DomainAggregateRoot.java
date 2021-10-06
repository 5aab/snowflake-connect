package com.example.cqrs.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mysema.commons.lang.Assert;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DomainAggregateRoot<E extends DomainEvent, ID extends Serializable> {

    @Transient
    @JsonIgnore
    private final transient List<E> domainEvents = new ArrayList<>();

    protected <T extends E> T registerEvent(T event) {
        Assert.notNull(event, "Domain event can't be null");
        this.domainEvents.add(event);
        return event;
    }

    @AfterDomainEventPublication
    protected void cleanDomainEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    public Collection<E> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public abstract ID getIdentity();

    public abstract String getAggregateName();

    @CreatedBy
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Invalid created by name '${validatedValue}', permissible characters are alphanumerical, underscore and hyphen.")
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String createdDate;

    @LastModifiedBy
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Invalid last modified by name '${validatedValue}', permissible characters are alphanumerical, underscore and hyphen.")
    @Column(name = "last_modified_by", length = 50)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String lastModifiedBy;


    @LastModifiedDate
    @Column(name = "last_modified_date", updatable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String lastModifiedDate;

    @Version
    private Integer version;
}
