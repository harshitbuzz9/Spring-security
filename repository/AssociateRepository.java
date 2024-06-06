package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssociateRepository extends JpaRepository<Associate,Integer> {
    Optional<Associate> findByPhone(String phone);
}
