package com.redisson.pessimistic;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PessimisticTicketRepository extends JpaRepository<PessimisticTicket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM PessimisticTicket t WHERE t.id = :id")
    PessimisticTicket findByIdWithPessimisticLock(Long id);

}
