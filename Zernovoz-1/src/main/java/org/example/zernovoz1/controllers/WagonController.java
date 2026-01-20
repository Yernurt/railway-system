package org.example.zernovoz1.controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.services.SuspiciousCacheService;
import org.example.zernovoz1.services.WagonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/wagons")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class WagonController {
    private final WagonService wagonService;
    private final SuspiciousCacheService suspiciousCacheService;


    @GetMapping("/{wagonNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER', 'ROLE_VIEWER')")
    public ResponseEntity<?> getWagonByNumber(@PathVariable String wagonNumber) {
        Optional<WagonModel> wagonOpt = wagonService.getWagonByNumber(wagonNumber);
        if (wagonOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Вагон табылмады!");
        }
        return ResponseEntity.ok(wagonOpt.get());
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER')")
    public ResponseEntity<WagonModel> addWagon(@RequestBody WagonModel wagon) {
        WagonModel savedWagon = wagonService.addWagon(wagon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWagon);
    }

    @GetMapping
    public ResponseEntity<List<WagonModel>> getAllWagons() {
        List<WagonModel> wagons = wagonService.getAllWagons();
        return ResponseEntity.ok(wagons);
    }

    @PutMapping("/{wagonNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_DISPATCHER', 'ROLE_OPERATOR')")
    public ResponseEntity<WagonModel> updateWagon(
            @PathVariable String wagonNumber,
            @RequestBody WagonModel updatedData) {
        WagonModel updatedWagon = wagonService.updateWagonData(wagonNumber, updatedData);
        return ResponseEntity.ok(updatedWagon);
    }
    @PatchMapping("/{wagonNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_DISPATCHER', 'ROLE_OPERATOR')")
    public ResponseEntity<WagonModel> patchWagon(
            @PathVariable String wagonNumber,
            @RequestBody Map<String, Object> updates) {
        WagonModel updatedWagon = wagonService.partialUpdateWagon(wagonNumber, updates);
        return ResponseEntity.ok(updatedWagon);
    }

    @DeleteMapping("/{wagonNumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteWagon(@PathVariable String wagonNumber) {
        boolean deleted = wagonService.deleteWagonByNumber(wagonNumber);
        if (deleted) {
            return ResponseEntity.ok("🚮 Вагон №" + wagonNumber + " жойылды.");
        } else {
            return ResponseEntity.status(404).body("❌ Вагон №" + wagonNumber + " табылмады.");
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER', 'ROLE_VIEWER')")
    public ResponseEntity<List<WagonModel>> filterWagons(
            @RequestParam(required = false) String wagonType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String departureStation,
            @RequestParam(required = false) String destinationStation,
            @RequestParam(required = false) String station // 👈 мұнда қосу
    ) {
        List<WagonModel> filteredWagons = wagonService.filterWagons(wagonType, status, departureStation, destinationStation, station);
        return ResponseEntity.ok(filteredWagons);
    }



    @GetMapping("/identified")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public ResponseEntity<List<WagonModel>> getAllIdentifiedWagons() {
        List<WagonModel> wagons = wagonService.getAllWagons().stream()
                .filter(w -> w.getIdentificationStatus() != null && !w.getIdentificationStatus().isBlank())
                .toList();

        return ResponseEntity.ok(wagons);
    }

    @PostMapping("/manual-fix")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public ResponseEntity<String> manualFix(@RequestBody Map<String, String> payload) {
        String suspiciousNumber = payload.get("suspiciousNumber");
        String correctNumber = payload.get("correctNumber");

        Optional<WagonModel> optionalWagon = wagonService.getWagonByNumber(correctNumber);
        if (optionalWagon.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Мұндай вагон базаға тіркелмеген!");
        }

        SuspiciousCacheService.SuspiciousData data = suspiciousCacheService.get(suspiciousNumber);
        if (data == null) {
            return ResponseEntity.badRequest().body("❌ Күдікті дерек табылмады.");
        }

        WagonModel wagon = optionalWagon.get();
        wagon.setStation(data.station);
        wagon.setLastUpdated(data.lastUpdated);
        wagon.setSpeedKmh(data.speedKmh);
        wagon.setIdentificationStatus(data.identificationStatus);
        wagon.setVideo(data.video);

        wagonService.saveWagon(wagon);
        suspiciousCacheService.remove(suspiciousNumber);

        return ResponseEntity.ok("✅ Деректер №" + correctNumber + " вагонға жазылды.");
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('ROLE_DISPATCHER', 'ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<List<WagonModel>> getAvailableWagons(@RequestParam String station) {
        return ResponseEntity.ok(wagonService.getAvailableWagonsForConsist(station));
    }


    @GetMapping("/auto-select")
    @PreAuthorize("hasAnyAuthority('ROLE_DISPATCHER', 'ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<List<WagonModel>> autoSelect(
            @RequestParam String type,
            @RequestParam double volume,
            @RequestParam(required = false) String station
    ) {
        return ResponseEntity.ok(wagonService.autoSelectWagons(type, volume, station));
    }

}