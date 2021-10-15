package com.example.cqrs.retry.domain;

import com.example.cqrs.persistence.CustomRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DeadMessageRepository extends CustomRepository<DeadMessage, UUID> {

    @Modifying
    @Lock(LockModeType.NONE)
    @Query("delete from DeadMessage de where de.createdDate <= ?1")
    void deleteExpiredDeadMessages(LocalDateTime expiryDate);
}
