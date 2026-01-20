package org.example.zernovoz1.controllers;

import lombok.RequiredArgsConstructor;
import org.example.zernovoz1.models.Users;
import org.example.zernovoz1.repositories.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepo userRepository;

    // 👀 1. Бекітілмеген, бірақ өтініш бергендер тізімі
    @GetMapping("/pending-users")
    public List<Users> getPendingUsers() {
        return userRepository.findByRoleIsNullAndRoleRequestIsNotNull();
    }

    // ✅ 2. Қолданушының рөлін бекіту
    @PostMapping("/approve-user/{id}")
    public ResponseEntity<String> approveUser(@PathVariable int id) {
        Optional<Users> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        Users user = optionalUser.get();

        if (user.getRoleRequest() == null) {
            return ResponseEntity.badRequest().body("Рөл сұралмаған");
        }

        user.setRole(user.getRoleRequest());
        user.setRoleRequest(null); // өтініш енді қажет емес
        userRepository.save(user);

        return ResponseEntity.ok("Рөл бекітілді: " + user.getRole());
    }

    // ❌ 3. Қолданушыны қабылдамау
    @PostMapping("/reject-user/{id}")
    public ResponseEntity<String> rejectUser(@PathVariable int id) {
        Optional<Users> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        Users user = optionalUser.get();
        user.setRoleRequest(null);
        userRepository.save(user);

        return ResponseEntity.ok("Рөл өтініші жойылды");
    }
}
