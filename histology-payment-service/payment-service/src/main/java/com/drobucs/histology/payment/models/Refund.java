package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "refunds", uniqueConstraints = @UniqueConstraint(columnNames = {"refundId", "paymentId"}))
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "userId")
    private long userId;
    @Column(name = "amountValue")
    private String amountValue;
    @Column(name = "currency")
    private String currency;
    @Column(name = "status")
    private String status;
    @Column(name = "description")
    private String description;
    @Column(name = "createdAt")
    private String createdAt;
    @Column(name = "refundId")
    private String refundId;
    @Column(name = "paymentId")
    private String paymentId;
}
