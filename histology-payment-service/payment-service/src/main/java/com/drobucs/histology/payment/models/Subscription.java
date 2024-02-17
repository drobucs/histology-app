package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nameSubscription"),
        name = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "nameSubscription")
    private String nameSubscription;
    @Column(name = "nameForUser")
    private String nameForUser;
    @Column(name = "price")
    private long price;
    @Column(name = "numberOfMonth")
    private long numberOfMonth;
    @Column(name = "descriptionForReceipt")
    private String descriptionForReceipt;
}
