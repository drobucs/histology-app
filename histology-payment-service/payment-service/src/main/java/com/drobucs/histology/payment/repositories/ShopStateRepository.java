package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.ShopState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopStateRepository extends JpaRepository<ShopState, Long> {
   ShopState getShopStateByShopStateName(String name);
}
