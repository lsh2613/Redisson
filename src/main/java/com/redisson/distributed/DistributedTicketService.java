package com.redisson.distributed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedTicketService {

    private final DistributedTicketRepository distributedTicketRepository;

    @Transactional
    public void ticketing(Long ticketId, Long quantity) {
        DistributedTicket distributedTicket = distributedTicketRepository.findById(ticketId).orElseThrow();
        distributedTicket.decrease(quantity);
        distributedTicketRepository.saveAndFlush(distributedTicket);
    }

    @RedissonLock(value = "#ticketId")
    public void ticketingWithRedisson(Long ticketId, Long quantity) {
        DistributedTicket distributedTicket = distributedTicketRepository.findById(ticketId).orElseThrow();
        distributedTicket.decrease(quantity);
        distributedTicketRepository.saveAndFlush(distributedTicket);
    }

}
