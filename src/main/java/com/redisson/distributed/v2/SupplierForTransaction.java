package com.redisson.distributed.v2;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class SupplierForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T get(Supplier<T> supplier) {
        return supplier.get();
    }

}
