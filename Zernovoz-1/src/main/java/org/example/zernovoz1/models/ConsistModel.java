package org.example.zernovoz1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "consists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsistModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consistNumber", nullable = false, unique = true)
    private String consistNumber; // унікалды состав номері

    @ManyToOne
    @JoinColumn(name = "locomotive_id")
    private LocomotiveModel locomotive; // составтың локомотиві

    @OneToMany(mappedBy = "consist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<WagonModel> wagons; // құрамындағы вагондар

    @Column(name = "station")
    private String station; // қай станцияда құрылды

    @Column(name = "status")
    private String status; // Статус: Жиналуда, Жолда, Жеткен

    @Column(name = "created_at")
    private String createdAt; // құрылған уақыты (String түрінде, кейін LocalDateTime қолдануға болады)
}
