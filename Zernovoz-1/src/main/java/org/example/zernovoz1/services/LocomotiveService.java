package org.example.zernovoz1.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.ConsistModel;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.repositories.LocomotiveRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class LocomotiveService {

    private final LocomotiveRepository locomotiveRepository;

    // Локомативті нөмері бойынша алу
    public Optional<LocomotiveModel> getLocomotiveByNumber(String LocomotiveNumber) {
        return locomotiveRepository.findByLocomotiveNumber(LocomotiveNumber);
    }

    // Барлық локомотивтерді алу
    public List<LocomotiveModel> getAllLocomotives() {
        return locomotiveRepository.findAll();
    }

    // Жаңа локомотив қосу
    public LocomotiveModel addLocomotive(LocomotiveModel locomotiveModel) {
        //Бос өрістерді стандартты мәндермен толтыру
        if (locomotiveModel.getIdentificationStatusLocomotive() == null)
            locomotiveModel.setIdentificationStatusLocomotive("");
        if (locomotiveModel.getDepartureStationLocomotive() == null) locomotiveModel.setDepartureStationLocomotive("");
        if (locomotiveModel.getDestinationStationLocomotive() == null)
            locomotiveModel.setDestinationStationLocomotive("");
        if (locomotiveModel.getSpeedKmhLocomotive() == null) locomotiveModel.setSpeedKmhLocomotive("0");
        if (locomotiveModel.getLastUpdatedLocomotive() == null) locomotiveModel.setLastUpdatedLocomotive("");
        if (locomotiveModel.getVideoLocomotive() == null) locomotiveModel.setVideoLocomotive("");
        return locomotiveRepository.save(locomotiveModel);
    }

    public LocomotiveModel updateStationLocomotive(String locomotiveNumber, String stationLocomotive, String lastUpdatedLocomotive, String speedKmhLocomotive, String statusLocomotive, String videoLocomotive) {
        LocomotiveModel locomotive = locomotiveRepository.findByLocomotiveNumber(locomotiveNumber)
                .orElseThrow(() -> new RuntimeException("Локомотив табылмады"));

        // Тек RabbitMQ-дан келген деректерді жаңарту
        locomotive.setStationLocomotive(stationLocomotive);
        locomotive.setLastUpdatedLocomotive(lastUpdatedLocomotive);
        locomotive.setSpeedKmhLocomotive(speedKmhLocomotive);
        locomotive.setIdentificationStatusLocomotive(statusLocomotive);
        locomotive.setVideoLocomotive(videoLocomotive);

        return locomotiveRepository.save(locomotive);
    }


    public LocomotiveModel partialUpdateLocomotive(String locomotiveNumber, Map<String, Object> updates) {
        LocomotiveModel locomotive = locomotiveRepository.findByLocomotiveNumber(locomotiveNumber)
                .orElseThrow(() -> new RuntimeException("Локомотив табылмады"));

        updates.forEach((key, value) -> {
            try {
                Field field = LocomotiveModel.class.getDeclaredField(key);
                field.setAccessible(true);

                if (value == null || value.toString().isEmpty()) {
                    log.warn("⚠️ {} үшін бос немесе NULL мән еленбейді.", key);
                    return;
                }

                // locomotiveNumber өзгерсе, жаңасын орнату
                if ("locomotiveNumber".equals(key)) {
                    log.info("🔄 locomotiveNumber өзгертілуде: {} → {}", locomotive.getLocomotiveNumber(), value.toString());
                    locomotive.setLocomotiveNumber(value.toString());
                } else {
                    field.set(locomotive, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("⚠️ Белгісіз өріс: {}", key);
            }
        });

        // Локомотивті қайта сақтау
        return locomotiveRepository.save(locomotive);
    }

    public LocomotiveModel updateLocomotiveData(String locomotiveNumber, LocomotiveModel updatedData) {
        LocomotiveModel locomotive = locomotiveRepository.findByLocomotiveNumber(locomotiveNumber)
                .orElseThrow(() -> new RuntimeException("Локомотив табылмады"));

        // Егер PUT сұранысында кейбір өрістер NULL болса, елемеу
        locomotive.setLocomotiveNumber(updatedData.getLocomotiveNumber());
        if (updatedData.getLocomotiveType() != null) locomotive.setLocomotiveType(updatedData.getLocomotiveType());
        if (updatedData.getStatusLocomotive() != null) locomotive.setStatusLocomotive(updatedData.getStatusLocomotive());
        if (updatedData.getDepartureStationLocomotive() != null) locomotive.setDepartureStationLocomotive(updatedData.getDepartureStationLocomotive());
        if (updatedData.getDestinationStationLocomotive() != null) locomotive.setDestinationStationLocomotive(updatedData.getDestinationStationLocomotive());
        if (updatedData.getIdentificationStatusLocomotive() != null) locomotive.setIdentificationStatusLocomotive(updatedData.getIdentificationStatusLocomotive());
        if (updatedData.getStationLocomotive() != null) locomotive.setStationLocomotive(updatedData.getStationLocomotive());
        if (updatedData.getSpeedKmhLocomotive() != null) locomotive.setSpeedKmhLocomotive(updatedData.getSpeedKmhLocomotive());
        if (updatedData.getLastUpdatedLocomotive() != null) locomotive.setLastUpdatedLocomotive(updatedData.getLastUpdatedLocomotive());
        if (updatedData.getVideoLocomotive() != null) locomotive.setVideoLocomotive(updatedData.getVideoLocomotive());
        return locomotiveRepository.save(locomotive);
    }


    public List<LocomotiveModel> filterLocomotives(String locomotiveType, String statusLocomotive, String departureStationLocomotive, String destinationStationLocomotive) {
        return locomotiveRepository.findAll().stream()
                .filter(wagon -> (locomotiveType == null || wagon.getLocomotiveType().equalsIgnoreCase(locomotiveType)))
                .filter(wagon -> (statusLocomotive == null || wagon.getStatusLocomotive().equalsIgnoreCase(statusLocomotive)))
                .filter(wagon -> (departureStationLocomotive == null || wagon.getDepartureStationLocomotive().equalsIgnoreCase(departureStationLocomotive)))
                .filter(wagon -> (destinationStationLocomotive == null || wagon.getDestinationStationLocomotive().equalsIgnoreCase(destinationStationLocomotive)))
                .toList();
    }

    public boolean deleteLocomotiveByNumber(String locomotiveNumber) {
        Optional<LocomotiveModel> locomotive = locomotiveRepository.findByLocomotiveNumber(locomotiveNumber);
        if (locomotive.isPresent()) {
            locomotiveRepository.delete(locomotive.get());
            return true;
        }
        return false;
    }


    public Optional<LocomotiveModel> getMostRecentlyUpdatedLocomotiveAtStation(String stationName) {
        return locomotiveRepository.findAll().stream()
                .filter(loco -> loco.getStationLocomotive() != null && loco.getStationLocomotive().equalsIgnoreCase(stationName))
                .filter(loco -> loco.getLastUpdatedLocomotive() != null && !loco.getLastUpdatedLocomotive().isBlank())
                .sorted((a, b) -> b.getLastUpdatedLocomotive().compareTo(a.getLastUpdatedLocomotive()))
                .findFirst();
    }
    public LocomotiveModel attachToConsist(LocomotiveModel loco, ConsistModel consist) {
        loco.setConsist(consist);
        return locomotiveRepository.save(loco);
    }


    // 🔹 Локомотивті сақтау (барлық өзгерістермен бірге)
    public LocomotiveModel saveLocomotive(LocomotiveModel locomotive) {
        return locomotiveRepository.save(locomotive);
    }

    // 🔹 Барлық локомотивтер санын есептеу
    public long countAllLocomotives() {
        return locomotiveRepository.count();
    }

    // 🔹 Белгілі бір статус бойынша локомотивтер санын есептеу
    public long countByStatus(String status) {
        return locomotiveRepository.countByStatusLocomotive(status);
    }
}



