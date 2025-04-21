package com.redisson.optimistic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class OptimisticTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quantity;

    @Version
    private Long version;

    public OptimisticTicket(Long quantity) {
        this.quantity = quantity;
    }

    public static OptimisticTicket create(Long quantity) {
        return new OptimisticTicket(quantity);
    }

    public void decrease(Long quantity) {
        long q = this.quantity - quantity;
        this.quantity = q < 0 ? 0L : q;
    }

}
