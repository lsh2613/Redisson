package com.redisson.distributed.v1;

import com.redisson.distributed.DistributedTicket;
import com.redisson.distributed.DistributedTicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class DistributedTicketServiceV1Test {

    @Autowired
    private DistributedTicketServiceV1 distributedTicketServiceV1;
    @Autowired
    private DistributedTicketRepository distributedTicketRepository;

    private final static Integer CONCURRENT_COUNT = 100;
    private static Long TICKET_ID = null;
    private final static StopWatch stopwatch = new StopWatch();

    @BeforeEach
    public void before() {
        log.info("1000개의 티켓 생성");
        DistributedTicket distributedTicket = new DistributedTicket(1000L);
        DistributedTicket saved = distributedTicketRepository.saveAndFlush(distributedTicket);
        TICKET_ID = saved.getId();
    }

    @AfterEach
    public void after() {
        distributedTicketRepository.deleteAll();
    }

    private void ticketingTest(Consumer<Void> action) throws InterruptedException {
        Long originQuantity = distributedTicketRepository.findById(TICKET_ID).orElseThrow().getQuantity();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    action.accept(null);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        DistributedTicket distributedTicket = distributedTicketRepository.findById(TICKET_ID).orElseThrow();
        assertEquals(originQuantity - CONCURRENT_COUNT, distributedTicket.getQuantity());
    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 동시성 이슈 발생")
    public void badTicketingTest() throws Exception {
        stopwatch.start("동시에 100명의 티켓팅 : 동시성 이슈 발생");
        ticketingTest((_no) -> distributedTicketServiceV1.ticketing(TICKET_ID, 1L));
        stopwatch.stop();

        System.out.println(stopwatch.prettyPrint());
    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 분산락")
    public void ticketingWithDistributedLock() throws Exception {
        stopwatch.start("동시에 100명의 티켓팅 : 분산락");
        ticketingTest((_no) -> distributedTicketServiceV1.ticketingWithRedisson(TICKET_ID, 1L));
        stopwatch.stop();

        System.out.println(stopwatch.prettyPrint());
    }

}
