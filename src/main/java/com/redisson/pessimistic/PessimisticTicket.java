package com.redisson.pessimistic;

import com.redisson.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@NoArgsConstructor
@ToString
public class PessimisticTicket implements Ticket {
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
}
