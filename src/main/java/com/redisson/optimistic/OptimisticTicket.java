package com.redisson.optimistic;

import com.redisson.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
public class OptimisticTicket implements Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quantity;

    @Version
    private Long version;

    public static OptimisticTicket of(Long quantity) {
        OptimisticTicket ticket = new OptimisticTicket();
        ticket.quantity = quantity;
        return ticket;
    }

    public void decrease(Long quantity) {
        long q = this.quantity - quantity;
        this.quantity = q < 0 ? 0L : q;
    }

}
