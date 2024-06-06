package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.LeadGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadGenerationRepository extends JpaRepository<LeadGeneration, Long>{
    Optional<LeadGeneration> findByLeadId(String id);
}
