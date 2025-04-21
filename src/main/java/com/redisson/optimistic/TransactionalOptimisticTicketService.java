package com.redisson.optimistic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionalOptimisticTicketService {

    private final OptimisticTicketRepository optimisticTicketRepository;

    @Transactional
    public void ticketing(Long ticketId, Long quantity) {
        OptimisticTicket optimisticTicket = optimisticTicketRepository.findByIdWithOptimisticLock(ticketId);
        optimisticTicket.decrease(quantity);
    }
}
