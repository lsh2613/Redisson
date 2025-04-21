package com.redisson.optimistic;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OptimisticTicketRepository extends JpaRepository<OptimisticTicket, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT t FROM OptimisticTicket t WHERE t.id = :id")
    OptimisticTicket findByIdWithOptimisticLock(@Param("id") Long id);
}
