package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop getShopByShopName(String name);
}
