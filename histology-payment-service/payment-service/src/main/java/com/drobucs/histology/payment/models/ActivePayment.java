package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Data
@Table(name = "activePayments", uniqueConstraints = @UniqueConstraint(columnNames = {"paymentId", "userId"}))
@AllArgsConstructor
@NoArgsConstructor
public class ActivePayment {
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

    @NotNull
    public static Payment convertToPayment(@NotNull ActivePayment activePayment) {
        return new Payment(
                activePayment.getId(),
                activePayment.getUserId(),
                activePayment.getPaymentId(),
                activePayment.getPaymentMethodType(),
                activePayment.getPaymentTime(),
                activePayment.getAmount(),
                activePayment.getCurrency(),
                activePayment.getStatus(),
                activePayment.getUuid(),
                activePayment.getSubscriptionId(),
                activePayment.getNeedConfirmed()
        );
    }

}
