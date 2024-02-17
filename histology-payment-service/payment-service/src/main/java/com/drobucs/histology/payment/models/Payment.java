package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "paymentId"))
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "userId")
    private long userId;
    @Column(name = "paymentId")
    private String paymentId;
    @Column(name = "paymentMethodType")
    private String paymentMethodType;
    @CreationTimestamp
    @Column(name = "paymentTime")
    private Date paymentTime;
    @Column(name = "amount")
    private double amount;
    @Column(name = "currency")
    private String currency;
    @Column(name = "status")
    private String status;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "subscriptionId")
    private Long subscriptionId;
    @Column(name = "needConfirmed")
    private Boolean needConfirmed;
}
