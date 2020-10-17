package com.company.main.entities;

import com.company.main.data.PetOrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private long id;
    private long petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;
}
