package org.example.zernovoz1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "locomotives")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocomotiveModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "locomotiveNumber", nullable = false, unique = true)
    private String locomotiveNumber;

    @Column(name="locomotiveType", nullable = false)
    private String locomotiveType; // 🔹 локоматив типі

    @Column(name="identificationStatusLocomotive")
    private String identificationStatusLocomotive; // Сканерлеу статусы (мысалы, "Нөмір анық емес")

    @Column(name="departureStationLocomotive" )
    private String departureStationLocomotive; // 🔹 Қай станциядан шыққаны

    @Column(name="destinationStationLocomotive", nullable = false)
    private String destinationStationLocomotive; // 🔹 Қай станцияға бағытталғаны

    @Column(name="stationLocomotive")
    private String stationLocomotive; // 🔹 Қай станцияға жеткені

    @Column(name = "statusLocomotive")
    private String statusLocomotive; // Мысалы: "Жиналуда", "Жөнелтілді", "Жолда", т.б.

    @Column(name = "speed_kmhLocomotive")
    private  String speedKmhLocomotive;

    @Column(name="lastUpdatedLocomotive")
    private String lastUpdatedLocomotive; // 🔹 Соңғы жаңартылған уақыты

    @Column(name = "videoLocomotive")
    private  String videoLocomotive;

    @OneToMany(mappedBy = "locomotive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WagonModel> wagons;

    @ManyToOne
    @JoinColumn(name = "consist_id")
    @JsonIgnore
    private ConsistModel consist;

}


