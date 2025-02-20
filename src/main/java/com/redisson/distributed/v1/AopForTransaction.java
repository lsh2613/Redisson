package com.redisson.distributed.v1;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * AOP에서 트랜잭션 분리를 위한 클래스
 */
@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    // 추가
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T proceed(Supplier<T> supplier) {
        return supplier.get();
    }
}
