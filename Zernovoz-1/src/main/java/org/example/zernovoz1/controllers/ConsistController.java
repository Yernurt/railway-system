package org.example.zernovoz1.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.ConsistModel;
import org.example.zernovoz1.models.LocomotiveModel;
import org.example.zernovoz1.models.WagonModel;
import org.example.zernovoz1.repositories.ConsistRepo;
import org.example.zernovoz1.services.ConsistService;
import org.example.zernovoz1.repositories.LocomotiveRepository;
import org.example.zernovoz1.repositories.WagonRepository;
import org.example.zernovoz1.services.ConsistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.zernovoz1.models.Users;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consists")
@AllArgsConstructor
@Slf4j

public class ConsistController {

    private final ConsistService consistService;

    // ✅ DTO осы жерде ішкі класс ретінде жазылады
    public static class ConsistRequest {
        private String locomotiveNumber;
        private List<Long> wagonIds;

        public String getLocomotiveNumber() {
            return locomotiveNumber;
        }

        public void setLocomotiveNumber(String locomotiveNumber) {
            this.locomotiveNumber = locomotiveNumber;
        }

        public List<Long> getWagonIds() {
            return wagonIds;
        }

        public void setWagonIds(List<Long> wagonIds) {
            this.wagonIds = wagonIds;
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER')")
    public ResponseEntity<String> createConsist(@RequestBody ConsistRequest request,
                                                @AuthenticationPrincipal Users user) {
        try {
            String station = user.getStation(); // ✅ Ағымдағы қолданушының станциясы
            String response = consistService.createConsist(request.getLocomotiveNumber(), request.getWagonIds(), station);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER','ROLE_OPERATOR')")
    public List<ConsistModel> getAll() {
        return consistService.getAllConsists();
    }

    @PatchMapping("/remove-wagon/{wagonId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER','ROLE_OPERATOR')")
    public ResponseEntity<String> removeWagonFromConsist(@PathVariable Long wagonId) {
        try {
            String result = consistService.removeWagonFromConsist(wagonId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/remove-wagon/{wagonId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER','ROLE_OPERATOR')")
    public ResponseEntity<String> removeWagon(@PathVariable Long wagonId) {
        try {
            String result = consistService.removeWagonFromConsist(wagonId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER','ROLE_OPERATOR')")
    public ResponseEntity<?> deleteConsist(@PathVariable Long id) {
        consistService.deleteConsist(id);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/by-locomotive/{number}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DISPATCHER','ROLE_OPERATOR')")
    public ResponseEntity<ConsistModel> getByLocomotiveNumber(@PathVariable String number) {
        return ResponseEntity.ok(consistService.getByLocomotiveNumber(number));
    }
}