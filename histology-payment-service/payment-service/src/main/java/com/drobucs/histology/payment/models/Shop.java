package com.drobucs.histology.payment.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "shops", uniqueConstraints = @UniqueConstraint(columnNames = {"shopName"}))
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "shopName")
    private String shopName;
    @Column(name = "shopId")
    private String shopId;
    @Column(name = "clientApplicationKey")
    private String clientApplicationKey;
    @Column(name = "authCenterClientId")
    private String authCenterClientId;

    public static Credentials convertToCredentials(Shop shop) {
        return new Credentials(shop.getShopId(), shop.getClientApplicationKey(), shop.getAuthCenterClientId());
    }
}
