package org.example.zernovoz1;

import lombok.extern.slf4j.Slf4j;
import org.example.zernovoz1.models.Users;
import org.example.zernovoz1.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@SpringBootApplication
public class Zernovoz1Application {

    public static void main(String[] args) {
        SpringApplication.run(Zernovoz1Application.class, args);
    }


    @Bean
    CommandLineRunner init(UserRepo userRepo) {
        return args -> {
            if (userRepo.findByUsername("admin") == null) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setName("Admin");
                admin.setSname("System");
                admin.setPhone("87000000000");
                admin.setStation(null);

                userRepo.save(admin);
                log.info("✅ Админ автоматты түрде қосылды: admin / admin123");
            }
        };
}}
