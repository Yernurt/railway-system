package org.example.zernovoz1.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="wagons")
@Data
@AllArgsConstructor
@NoArgsConstructor


public class WagonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="wagonNumber",  nullable = false)
    private String wagonNumber; // Вагонның бірегей нөмірі

    @Column(name="wagonType", nullable = false)
    private String wagonType; // 🔹 Вагонның типі (мысалы: "крытый", "зерновоз", "платформа" т.б.)

    @Column(name="status")
    private String status; // 🔹 Вагон пустой ма, әлде жүк тиелген бе?


    @Column(name="identificationStatus")
    private String identificationStatus; // Сканерлеу статусы (мысалы, "Нөмір анық емес")

    @Column(name="departureStation" )
    private String departureStation; // 🔹 Қай станциядан шыққаны

    @Column(name="destinationStation", nullable = false)
    private String destinationStation; // 🔹 Қай станцияға бағытталғаны

    @Column(name="station")
    private String station; // 🔹 Қай станцияға жеткені

    @Column(name="cargoType")
    private String cargoType; // 🔹 Қандай астық тиегені (мысалы: "бидай", "арпа", "жүгері")

    @Column(name="cargoVolume")
    private Double cargoVolume; // 🔹 Жүктің көлемі (тонна немесе м³)

    @Column(name="lastUpdated")
    private String lastUpdated; // 🔹 Соңғы жаңартылған уақыты

    @Column(name = "speed_kmh")
    private  String speedKmh;

    @Column(name = "video")
    private  String video;
    @ManyToOne
    @JoinColumn(name = "locomotive_id")  // Вагон таблицасында locomotive_id деген баған болады
    @JsonIgnore
    private LocomotiveModel locomotive;

    @ManyToOne
    @JoinColumn(name = "consist_id")
    @JsonBackReference
    private ConsistModel consist;

}
