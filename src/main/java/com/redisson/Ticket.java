package com.redisson;

public interface Ticket {
    Long getId();

    Long getQuantity();

    void setQuantity(Long quantity);

    default void decrease(Long quantity) {
        long q = getQuantity() - quantity;
        setQuantity(q < 0 ? 0L : q);
    }
}
