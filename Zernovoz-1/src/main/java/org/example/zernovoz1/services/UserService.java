package org.example.zernovoz1.services;


import org.example.zernovoz1.models.Users;
import org.example.zernovoz1.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepo repo;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));

        // Қолданушы рөл сұрайды, бірақ жүйе нақты рөл бермейді
        if (user.getRoleRequest() == null || user.getRoleRequest().isEmpty()) {
            throw new IllegalArgumentException("Қолданушы рөлге өтініш білдіруі тиіс.");
        }

        // Бастапқыда нақты рөл берілмейді
        user.setRole(null);
        return repo.save(user);
    }


    public String verify(Users user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken((UserDetails)authentication.getPrincipal());
        } else {
            return "fail";
        }
    }
}