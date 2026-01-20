package org.example.zernovoz1.repositories;

import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocomotiveRepository extends JpaRepository<LocomotiveModel,Long> {
    Optional<LocomotiveModel> findByLocomotiveNumber(String locomotiveNumber);
    List<LocomotiveModel> findAll();

    @Query("SELECT COUNT(l) FROM LocomotiveModel l WHERE l.statusLocomotive = :statusLocomotive")
    long countByStatusLocomotive(@Param("statusLocomotive") String statusLocomotive);
}
