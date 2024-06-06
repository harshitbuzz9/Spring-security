package com.bridge.herofincorp.repository;

import com.bridge.herofincorp.model.entities.AssociateRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociateRoleRepository extends JpaRepository<AssociateRole,Integer> {

    List<AssociateRole> findAllByStaffId(Integer staffId);
    @Transactional
    void deleteByStaffId(Integer staffId);
}
