package org.example.zernovoz1.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.repositories.WagonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.stream.Collectors;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class WagonService {
    private final WagonRepository wagonRepository;

    // 🔹 Вагонды нөмірі бойынша табу (Optional)
    public Optional<WagonModel> getWagonByNumber(String wagonNumber) {
        return wagonRepository.findByWagonNumber(wagonNumber);
    }

    // 🔹 Барлық вагондарды шығару
    public List<WagonModel> getAllWagons() {
        return wagonRepository.findAll();
    }

    // 🔹 Жаңа вагон қосу
    public WagonModel addWagon(WagonModel wagonModel) {
        // Бос өрістерді стандартты мәндермен толтыру
        if (wagonModel.getIdentificationStatus() == null) wagonModel.setIdentificationStatus("");
        if (wagonModel.getDepartureStation() == null) wagonModel.setDepartureStation("");
        if (wagonModel.getDestinationStation() == null) wagonModel.setDestinationStation("");
        if (wagonModel.getSpeedKmh() == null) wagonModel.setSpeedKmh("0");
        if (wagonModel.getLastUpdated() == null) wagonModel.setLastUpdated(null);
        if(wagonModel.getVideo() == null) wagonModel.setVideo("");
        return wagonRepository.save(wagonModel);
    }
    public WagonModel updateStation(String wagonNumber, String station, String lastUpdated, String speedKmh, String status, String video) {
        WagonModel wagon = wagonRepository.findByWagonNumber(wagonNumber)
                .orElseThrow(() -> new RuntimeException("Вагон табылмады"));

        // Тек RabbitMQ-дан келген деректерді жаңарту
        wagon.setStation(station);
        wagon.setLastUpdated(lastUpdated);
        wagon.setSpeedKmh(speedKmh);
        wagon.setIdentificationStatus(status);
        wagon.setVideo(video);

        return wagonRepository.save(wagon);
    }

    public WagonModel partialUpdateWagon(String wagonNumber, Map<String, Object> updates) {
        WagonModel wagon = wagonRepository.findByWagonNumber(wagonNumber)
                .orElseThrow(() -> new RuntimeException("Вагон табылмады"));

        updates.forEach((key, value) -> {
            try {
                Field field = WagonModel.class.getDeclaredField(key);
                field.setAccessible(true);

                if (value == null || value.toString().isEmpty()) {
                    log.warn("⚠️ {} үшін бос немесе NULL мән еленбейді.", key);
                    return;
                }

                // wagonNumber өзгерсе, жаңасын орнату
                if ("wagonNumber".equals(key)) {
                    log.info("🔄 wagonNumber өзгертілуде: {} → {}", wagon.getWagonNumber(), value.toString());
                    wagon.setWagonNumber(value.toString());
                } else {
                    field.set(wagon, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("⚠️ Белгісіз өріс: {}", key);
            }
        });

        // Вагонды қайта сақтау
        return wagonRepository.save(wagon);
    }



    public WagonModel updateWagonData(String wagonNumber, WagonModel updatedData) {
        WagonModel wagon = wagonRepository.findByWagonNumber(wagonNumber)
                .orElseThrow(() -> new RuntimeException("Вагон табылмады"));

        // Егер PUT сұранысында кейбір өрістер NULL болса, елемеу
        wagon.setWagonNumber(updatedData.getWagonNumber());
        if (updatedData.getWagonType() != null) wagon.setWagonType(updatedData.getWagonType());
        if (updatedData.getStatus() != null) wagon.setStatus(updatedData.getStatus());
        if (updatedData.getDepartureStation() != null) wagon.setDepartureStation(updatedData.getDepartureStation());
        if (updatedData.getDestinationStation() != null) wagon.setDestinationStation(updatedData.getDestinationStation());
        if (updatedData.getCargoType() != null) wagon.setCargoType(updatedData.getCargoType());
        if (updatedData.getCargoVolume() != null) wagon.setCargoVolume(updatedData.getCargoVolume());
        if (updatedData.getIdentificationStatus() != null) wagon.setIdentificationStatus(updatedData.getIdentificationStatus());
        if (updatedData.getStation() != null) wagon.setStation(updatedData.getStation());
        if (updatedData.getSpeedKmh() != null) wagon.setSpeedKmh(updatedData.getSpeedKmh());
        if (updatedData.getLastUpdated() != null) wagon.setLastUpdated(updatedData.getLastUpdated());
        if (updatedData.getVideo() != null) wagon.setVideo(updatedData.getVideo());
        return wagonRepository.save(wagon);
    }


    public boolean deleteWagonByNumber(String wagonNumber) {
        Optional<WagonModel> wagon = wagonRepository.findByWagonNumber(wagonNumber);
        if (wagon.isPresent()) {
            wagonRepository.delete(wagon.get());
            return true;
        }
        return false;
    }

    // WagonService ішіне мына әдісті қос:
    public List<WagonModel> autoSelectWagons(String type, double targetVolume, String station) {
        return wagonRepository.findAll().stream()
                .filter(w -> "бос".equalsIgnoreCase(w.getStatus())) // тек бос
                .filter(w -> w.getWagonType().equalsIgnoreCase(type))
                .filter(w -> station == null || station.equalsIgnoreCase(w.getStation()))
                .sorted(Comparator.comparing(WagonModel::getCargoVolume).reversed()) // үлкеннен кішіге
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), list -> {
                            double sum = 0;
                            List<WagonModel> selected = new java.util.ArrayList<>();
                            for (WagonModel w : list) {
                                if (sum + w.getCargoVolume() <= targetVolume) {
                                    selected.add(w);
                                    sum += w.getCargoVolume();
                                }
                                if (sum >= targetVolume) break;
                            }
                            return selected;
                        }
                ));
    }



    public List<WagonModel> filterWagons(String wagonType, String status, String departureStation, String destinationStation, String station) {
        return wagonRepository.findAll().stream()
                .filter(wagon -> (wagonType == null || wagon.getWagonType().equalsIgnoreCase(wagonType)))
                .filter(wagon -> (status == null || wagon.getStatus().equalsIgnoreCase(status)))
                .filter(wagon -> (departureStation == null || wagon.getDepartureStation().equalsIgnoreCase(departureStation)))
                .filter(wagon -> (destinationStation == null || wagon.getDestinationStation().equalsIgnoreCase(destinationStation)))
                .filter(w -> station == null || // ✅ осы 3 шарттың бірі true болса
                        station.equalsIgnoreCase(w.getStation()) ||
                        station.equalsIgnoreCase(w.getDepartureStation()) ||
                        station.equalsIgnoreCase(w.getDestinationStation())
                )
                .toList();
    }

    public List<WagonModel> getAvailableWagonsForConsist(String station) {
        return wagonRepository.findAll().stream()
                .filter(w -> "бос".equalsIgnoreCase(w.getStatus()))
                .filter(w -> w.getStation() != null && w.getStation().equalsIgnoreCase(station))
                .sorted(Comparator.comparing(WagonModel::getLastUpdated).reversed()) // опционалды: соңғы жаңартылғандар
                .collect(Collectors.toList());
    }


    // 🔹 Вагонды сақтау (барлық өзгерістермен бірге)

    public WagonModel saveWagon(WagonModel wagon) {
        return wagonRepository.save(wagon);
    }

    // 🔹 Барлық вагондар санын есептеу
    public long countAllWagons() {
        return wagonRepository.count();
    }

    // 🔹 Белгілі бір статус бойынша вагондар санын есептеу
    public long countByStatus(String status) {
        return wagonRepository.countByStatus(status);
    }
}
