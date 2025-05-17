package com.redisson.distributed.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributedLockManager {
    private final RedissonClient redissonClient;
    private final SupplierForTransaction supplierForTransaction;
    private final String PREFIX = "RedissonLock-";

    public <T> T lock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(PREFIX + lockKey);
        try {
            boolean lockable = lock.tryLock(50000, 20000, TimeUnit.MILLISECONDS);
            if (!lockable) {
                log.info("Lock 획득 실패={}", lockKey);
                throw new RuntimeException("Lock 획득 실패");
            }

            log.info("락 획득 및 로직 수행");
            return supplierForTransaction.get(supplier);
        } catch (InterruptedException e) {
            log.error("락을 획득하는 중 에러 발생", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("락 해제");
                lock.unlock();
            }
        }

        throw new RuntimeException("Lock 획득 실패");
    }
}
