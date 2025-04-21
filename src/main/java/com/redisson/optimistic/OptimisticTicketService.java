package com.redisson.optimistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OptimisticTicketService {
    private final TransactionalOptimisticTicketService transactionalService;

    public void ticketing(Long ticketId, Long quantity) {
        while (true) {
            try {
                transactionalService.ticketing(ticketId, quantity);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.info("티케팅에 실패했습니다. 다시 시도합니다.");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
