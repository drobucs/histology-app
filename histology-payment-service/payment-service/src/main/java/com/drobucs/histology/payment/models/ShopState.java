package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "shopState", uniqueConstraints = @UniqueConstraint(columnNames = {"shopStateName"}))
@NoArgsConstructor
@AllArgsConstructor
public class ShopState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "shopStateName")
    private String shopStateName;
    @Column(name = "activeShop")
    private String activeShop;
}
