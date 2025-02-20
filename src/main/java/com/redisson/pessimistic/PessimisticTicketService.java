package com.redisson.pessimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
