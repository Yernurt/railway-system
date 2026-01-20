package org.example.zernovoz1.controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.ConsistModel;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.services.ConsistService;
import org.example.zernovoz1.services.LocomotiveService;
import org.example.zernovoz1.services.SuspiciousCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/locomotives")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class LocomotiveController {
    private final SuspiciousCacheService suspiciousCacheService;
    private final LocomotiveService locomotiveService;
    private final ConsistService consistService;


    @GetMapping("/{locomotiveNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER', 'ROLE_VIEWER')")
    public ResponseEntity<?> getlocomotiveByNumber(@PathVariable String locomotiveNumber) {
        Optional<LocomotiveModel> locomotiveOpt = locomotiveService.getLocomotiveByNumber(locomotiveNumber);
        if (locomotiveOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Локомотив табылмады!");
        }
        return ResponseEntity.ok(locomotiveOpt.get());
    }



    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER')")
    public ResponseEntity<LocomotiveModel> addLocomotive(@RequestBody LocomotiveModel locomotive) {
        LocomotiveModel savedLocomotive = locomotiveService.addLocomotive(locomotive);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocomotive);
    }

    @GetMapping
    public ResponseEntity<List<LocomotiveModel>> getAllLocomotives() {
        List<LocomotiveModel> locomotives = locomotiveService.getAllLocomotives();
        return ResponseEntity.ok(locomotives);
    }

    @PutMapping("/{locomotiveNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_DISPATCHER', 'ROLE_OPERATOR')")
    public ResponseEntity<LocomotiveModel> updateLocomotive(
            @PathVariable String locomotiveNumber,
            @RequestBody LocomotiveModel updatedData) {
        LocomotiveModel updateLocomotive = locomotiveService.updateLocomotiveData(locomotiveNumber, updatedData);
        return ResponseEntity.ok(updateLocomotive);
    }
    @PatchMapping("/{locomotiveNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_DISPATCHER', 'ROLE_OPERATOR')")
    public ResponseEntity<LocomotiveModel> patchLocomotive(
            @PathVariable String locomotiveNumber,
            @RequestBody Map<String, Object> updates) {
        LocomotiveModel updatedLocomotive = locomotiveService.partialUpdateLocomotive(locomotiveNumber, updates);
        return ResponseEntity.ok(updatedLocomotive);
    }

    @DeleteMapping("/{locomotiveNumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteLocomotive(@PathVariable String locomotiveNumber) {
        boolean deleted = locomotiveService.deleteLocomotiveByNumber(locomotiveNumber);
        if (deleted) {
            return ResponseEntity.ok("🚮 Локомотив №" + locomotiveNumber + " жойылды.");
        } else {
            return ResponseEntity.status(404).body("❌ Локомотив №" + locomotiveNumber + " табылмады.");
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER', 'ROLE_VIEWER')")
    public ResponseEntity<List<LocomotiveModel>> filterLocomotives(
            @RequestParam(required = false) String locomotiveType,
            @RequestParam(required = false) String statusLocomotive,
            @RequestParam(required = false) String departureStationLocomotive,
            @RequestParam(required = false) String destinationStationLocomotive
    ) {
        List<LocomotiveModel> filterLocomotives = locomotiveService.filterLocomotives(locomotiveType, statusLocomotive, departureStationLocomotive, destinationStationLocomotive);
        return ResponseEntity.ok(filterLocomotives);
    }



    @GetMapping("/identified")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public ResponseEntity<List<LocomotiveModel>> getAllIdentifiedLocomotives() {
        List<LocomotiveModel> locomotives = locomotiveService.getAllLocomotives().stream()
                .filter(w -> w.getIdentificationStatusLocomotive() != null && !w.getIdentificationStatusLocomotive().isBlank())
                .toList();

        return ResponseEntity.ok(locomotives);
    }

    @PostMapping("/manual-fix")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public ResponseEntity<String> manualFix(@RequestBody Map<String, String> payload) {
        String suspiciousNumber = payload.get("suspiciousNumber");
        String correctNumber = payload.get("correctNumber");

        Optional<LocomotiveModel> optionalLocomotive = locomotiveService.getLocomotiveByNumber(correctNumber);
        if (optionalLocomotive.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Мұндай локомотив базаға тіркелмеген!");
        }

        SuspiciousCacheService.SuspiciousData data = suspiciousCacheService.get(suspiciousNumber);
        if (data == null) {
            return ResponseEntity.badRequest().body("❌ Күдікті дерек табылмады.");
        }

        LocomotiveModel locomotive = optionalLocomotive.get();
        locomotive.setStationLocomotive(data.station);
        locomotive.setLastUpdatedLocomotive(data.lastUpdated);
        locomotive.setSpeedKmhLocomotive(data.speedKmh);
        locomotive.setIdentificationStatusLocomotive(data.identificationStatus);
        locomotive.setVideoLocomotive(data.video);

        locomotiveService.saveLocomotive(locomotive);
        suspiciousCacheService.remove(suspiciousNumber);

        return ResponseEntity.ok("✅ Деректер №" + correctNumber + " локомотивке жазылды.");
    }

}
