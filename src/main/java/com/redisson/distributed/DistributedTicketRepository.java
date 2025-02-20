package com.redisson.distributed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributedTicketRepository extends JpaRepository<DistributedTicket, Long> {
}
