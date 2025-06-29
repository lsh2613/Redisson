package com.redisson.optimistic;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class OptimisticTicketServiceTest {

    @Autowired
    private OptimisticTicketService optimisticTicketService;
    @Autowired
    private OptimisticTicketRepository optimisticTicketRepository;

    private final static Integer CONCURRENT_COUNT = 100;
    private static Long TICKET_ID = null;
    private static StopWatch stopWatch = new StopWatch();

    @BeforeEach
    public void before() {
        log.info("1000개의 티켓 생성");
        OptimisticTicket optimisticTicket = OptimisticTicket.of(1000L);
        OptimisticTicket saved = optimisticTicketRepository.saveAndFlush(optimisticTicket);
        TICKET_ID = saved.getId();
    }

    @AfterEach
    public void after() {
        optimisticTicketRepository.deleteAll();
    }

    private void ticketingTest() throws InterruptedException {
        Long originQuantity = optimisticTicketRepository.findById(TICKET_ID).orElseThrow().getQuantity();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    optimisticTicketService.ticketing(TICKET_ID, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        OptimisticTicket optimisticTicket = optimisticTicketRepository.findById(TICKET_ID).orElseThrow();
        assertEquals(originQuantity - CONCURRENT_COUNT, optimisticTicket.getQuantity());
    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 낙관적 락")
    public void redissonTicketingTest() throws Exception {
        stopWatch.start("동시에 100명의 티켓팅 : 낙관적 락");
        ticketingTest();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }

}
