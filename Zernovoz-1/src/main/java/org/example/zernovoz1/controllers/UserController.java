package org.example.zernovoz1.controllers;

import lombok.AllArgsConstructor;
import org.example.zernovoz1.models.Users;
import org.example.zernovoz1.repositories.UserRepo;
import org.example.zernovoz1.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users") // 👈 Енді дұрыс
@AllArgsConstructor
public class UserController {

    private final UserService service;
    private final UserRepo userRepo;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Users> addUser(@RequestBody Users user) {
        return ResponseEntity.ok(service.register(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        Optional<Users> user = userRepo.findById(id);
        return user.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("❌ Пайдаланушы табылмады"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return ResponseEntity.ok("🗑️ Пайдаланушы жойылды");
        } else {
            return ResponseEntity.status(404).body("❌ Пайдаланушы табылмады");
        }
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateRole(@PathVariable int id, @RequestBody String newRole) {
        Optional<Users> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Пайдаланушы табылмады");
        }

        Users user = userOpt.get();
        user.setRole(newRole.replace("\"", ""));
        userRepo.save(user);

        return ResponseEntity.ok("✅ Рөл жаңартылды: " + newRole);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Users>> searchByUsername(@RequestParam("username") String username) {
        Users user = userRepo.findByUsername(username);
        return ResponseEntity.ok(Collections.singletonList(user));
    }
}
