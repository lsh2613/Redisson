package com.redisson.distributed.v1;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {

    String value(); // Lock의 이름 (고유값)

    long waitTime() default 50000L; // Lock획득을 시도하는 최대 시간 (ms)

    long leaseTime() default 20000L; // 락을 획득한 후, 점유하는 최대 시간 (ms)

}
