package com.redisson.distributed.v2;

import com.redisson.distributed.DistributedTicket;
import com.redisson.distributed.DistributedTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static jodd.util.ThreadUtil.sleep;

@Service
@RequiredArgsConstructor
public class DistributedTicketServiceV2 {

    private final DistributedLockManager distributedLockManager;
    private final DistributedTicketRepository distributedTicketRepository;

    public void ticketingWithRedisson(Long ticketId, Long quantity) {
        sleep(500);

        distributedLockManager.lock(
                String.valueOf(ticketId),
                () -> {
                    ticketing(ticketId, quantity);
                    return null;
                });
    }

    private void ticketing(Long ticketId, Long quantity) {
        DistributedTicket distributedTicket = distributedTicketRepository.findById(ticketId).orElseThrow();
        distributedTicket.decrease(quantity);
    }

}
