package com.redisson.pessimistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PessimisticTicketService {
    private final PessimisticTicketRepository pessimisticTicketRepository;

    @Transactional
    public void ticketing(Long ticketId, Long quantity) {
        PessimisticTicket pessimisticTicket = pessimisticTicketRepository.findByIdWithPessimisticLock(ticketId);
        pessimisticTicket.decrease(quantity);
        pessimisticTicketRepository.saveAndFlush(pessimisticTicket);
    }
}
