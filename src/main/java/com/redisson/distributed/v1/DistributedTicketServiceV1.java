package com.redisson.distributed.v1;

import com.redisson.distributed.DistributedTicket;
import com.redisson.distributed.DistributedTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jodd.util.ThreadUtil.sleep;

@Service
@RequiredArgsConstructor
public class DistributedTicketServiceV1 {

    private final DistributedTicketRepository distributedTicketRepository;

    @Transactional
    public void ticketingWithoutRedisson(Long ticketId, Long quantity) {
        DistributedTicket distributedTicket = distributedTicketRepository.findById(ticketId).orElseThrow();
        distributedTicket.decrease(quantity);
    }

    @RedissonLock(value = "#ticketId")
    public void ticketingWithRedisson(Long ticketId, Long quantity) {
        sleep(500);

        DistributedTicket distributedTicket = distributedTicketRepository.findById(ticketId).orElseThrow();
        distributedTicket.decrease(quantity);
    }

}
