package com.rajesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT_TBL")
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String mode;
    @Column
    private Long orderId;
    @Column
    private double amount;
    @Column
    private String status;
}
