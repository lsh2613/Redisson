package com.redisson.pessimistic;

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
class PessimisticTicketServiceTest {

    @Autowired
    private PessimisticTicketService pessimisticTicketService;
    @Autowired
    private PessimisticTicketRepository pessimisticTicketRepository;

    private final static Integer CONCURRENT_COUNT = 100;
    private static Long TICKET_ID = null;
    private final static StopWatch stopwatch = new StopWatch();

    @BeforeEach
    public void before() {
        log.info("1000개의 티켓 생성");
        PessimisticTicket optimisticTicket = new PessimisticTicket(1000L);
        PessimisticTicket saved = pessimisticTicketRepository.saveAndFlush(optimisticTicket);
        TICKET_ID = saved.getId();
    }

    @AfterEach
    public void after() {
        pessimisticTicketRepository.deleteAll();
    }

    private void ticketingTest() throws InterruptedException {
        Long originQuantity = pessimisticTicketRepository.findById(TICKET_ID).orElseThrow().getQuantity();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticTicketService.ticketing(TICKET_ID, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        PessimisticTicket optimisticTicket = pessimisticTicketRepository.findById(TICKET_ID).orElseThrow();
        assertEquals(originQuantity - CONCURRENT_COUNT, optimisticTicket.getQuantity());
    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 비관적 락")
    public void redissonTicketingTest() throws Exception {
        stopwatch.start("동시에 100명의 티켓팅 : 비관적 락");
        ticketingTest();
        stopwatch.stop();

        log.info(stopwatch.prettyPrint());
    }

}
