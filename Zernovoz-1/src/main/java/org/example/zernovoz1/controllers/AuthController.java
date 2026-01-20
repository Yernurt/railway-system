package org.example.zernovoz1.controllers;

import lombok.AllArgsConstructor;
import org.example.zernovoz1.models.Users;
import org.example.zernovoz1.repositories.UserRepo;
import org.example.zernovoz1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping // 🔹 Бүкіл маршрут /users/... басталады
@AllArgsConstructor
public class AuthController {


    private UserService service;


    // 🔹 Пайдаланушыны тіркеу (жалпы /register)
    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) {
        return ResponseEntity.ok(service.register(user));
    }

    // 🔹 Логин
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user) {
        return ResponseEntity.ok(service.verify(user));
    }


}
