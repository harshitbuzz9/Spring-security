package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Dealer findByDealerCode(String dealerCode);
}
