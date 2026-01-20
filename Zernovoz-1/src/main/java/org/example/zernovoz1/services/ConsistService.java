package org.example.zernovoz1.services;
import lombok.RequiredArgsConstructor;
import org.example.zernovoz1.models.ConsistModel;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.repositories.ConsistRepo;
import org.example.zernovoz1.repositories.LocomotiveRepository;
import org.example.zernovoz1.repositories.WagonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor

public class ConsistService {

    private final ConsistRepo consistRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final WagonRepository wagonRepository;

    public String createConsist(String locomotiveNumber, List<Long> wagonIds, String station) {
        LocomotiveModel loco = locomotiveRepository.findByLocomotiveNumber(locomotiveNumber)
                .orElseThrow(() -> new RuntimeException("❌ Локомотив табылмады!"));

        ConsistModel consist = new ConsistModel();
        consist.setConsistNumber("Состав - " + loco.getLocomotiveNumber());
        consist.setLocomotive(loco);
        consist.setStation(station); // 🟢 пайдаланушы станциясы



        List<WagonModel> wagons = wagonRepository.findAllById(wagonIds);
        for (WagonModel w : wagons) {
            if (w.getConsist() != null) {
                throw new RuntimeException("🚫 Вагон №" + w.getWagonNumber() + " басқа составқа тіркелген!");
            }
            w.setConsist(consist);
        }

        consist.setWagons(wagons);
        consistRepository.save(consist);
        wagonRepository.saveAll(wagons);

        return "✅ Состав сәтті құрылды!";
    }

    public List<ConsistModel> getAllConsists() {
        return consistRepository.findAll();
    }


    public ConsistModel getByLocomotiveNumber(String number) {
        LocomotiveModel loco = locomotiveRepository.findByLocomotiveNumber(number)
                .orElseThrow(() -> new RuntimeException("🚫 Локомотив табылмады: " + number));

        return consistRepository.findByLocomotive(loco)
                .orElseThrow(() -> new RuntimeException("🚫 Состав табылмады: " + number));
    }
    @Transactional
    public String removeWagonFromConsist(Long wagonId) {
        WagonModel wagon = wagonRepository.findById(wagonId)
                .orElseThrow(() -> new RuntimeException("❌ Вагон табылмады"));

        ConsistModel consist = wagon.getConsist();
        if (consist == null) {
            throw new RuntimeException("❌ Бұл вагон ешқандай составқа тіркелмеген");
        }

        wagon.setConsist(null);
        wagonRepository.save(wagon);

        // ❗ Составтағы басқа вагондар бар ма, соны тексереміз
        List<WagonModel> remaining = wagonRepository.findByConsist_Id(consist.getId());
        if (remaining.isEmpty()) {
            consistRepository.delete(consist);
            return "ℹ️ Вагон шығарылды. Состав бос болғандықтан жойылды.";
        }

        return "✅ Вагон составтан шығарылды.";
    }

    @Transactional
    public void deleteConsist(Long id) {
        ConsistModel consist = consistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Состав табылмады: " + id));

        // 1. Барлық вагондарды құрамынан ажырату
        if (consist.getWagons() != null) {
            for (WagonModel wagon : consist.getWagons()) {
                wagon.setConsist(null);
            }
            wagonRepository.saveAll(consist.getWagons());
        }

        // 2. Составты жою
        consistRepository.delete(consist);
    }

}
