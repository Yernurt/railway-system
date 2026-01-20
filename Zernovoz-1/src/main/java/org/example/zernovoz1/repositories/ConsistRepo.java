package org.example.zernovoz1.repositories;
import org.example.zernovoz1.models.ConsistModel;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsistRepo extends JpaRepository<ConsistModel, Long> {
    Optional<ConsistModel> findByLocomotive(LocomotiveModel locomotive);
}