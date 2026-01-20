package org.example.zernovoz1.repositories;

import lombok.RequiredArgsConstructor;
import org.example.zernovoz1.models.WagonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface WagonRepository extends JpaRepository<WagonModel,Long> {
    Optional<WagonModel> findByWagonNumber(String wagonNumber);
    List<WagonModel> findAll();

    List<WagonModel> findByConsist_Id(Long consistId);


    @Query("SELECT w FROM WagonModel w WHERE w.wagonType = :type AND w.station = :station AND w.status = 'бос'")
    List<WagonModel> autoSelect(@Param("type") String type, @Param("station") String station);

    @Query("SELECT COUNT(w) FROM WagonModel w WHERE w.status = :status")
    long countByStatus(@Param("status") String status);
}
