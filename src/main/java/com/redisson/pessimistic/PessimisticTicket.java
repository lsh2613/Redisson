package com.redisson.pessimistic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class PessimisticTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quantity;

    public PessimisticTicket(Long quantity) {
        this.quantity = quantity;
    }

    public static PessimisticTicket create(Long quantity) {
        return new PessimisticTicket(quantity);
    }

    public void decrease(Long quantity) {
        long q = this.quantity - quantity;
        this.quantity = q < 0 ? 0L : q;
    }

}
