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
        while (true) {
            try {
                PessimisticTicket pessimisticTicket = pessimisticTicketRepository.findByIdWithPessimisticLock(ticketId);
                System.out.println("pessimisticTicket = " + pessimisticTicket);
                pessimisticTicket.decrease(quantity);
                pessimisticTicketRepository.saveAndFlush(pessimisticTicket);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                try {
                    log.info("티케팅에 실패했습니다..");
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
