package org.example.zernovoz1.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS қосу
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("login", "register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER")
                        .requestMatchers(HttpMethod.GET, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR", "ROLE_VIEWER")
                        .requestMatchers(HttpMethod.PUT, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/wagons/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/dispatch/**").hasAuthority("ROLE_DISPATCHER")
                        .requestMatchers(HttpMethod.POST, "/consists/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.GET, "/consists/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers("/operator/**").hasAuthority("ROLE_OPERATOR")
                        .requestMatchers("/wagons/check/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.POST, "/api/routes/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/routes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/routes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/routes/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/wagons/manual-fix").hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERATOR")
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
       /* return http.csrf(customizer -> customizer.disable()).
                authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER") // ✅ Тек POST үшін шектеу
                        .requestMatchers(HttpMethod.GET, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR", "ROLE_VIEWER")
                        .requestMatchers(HttpMethod.PUT, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/wagons/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/wagons/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DISPATCHER", "ROLE_OPERATOR")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/dispatch/**").hasAuthority("ROLE_DISPATCHER")
                        .requestMatchers("/operator/**").hasAuthority("ROLE_OPERATOR")
                        .anyRequest().authenticated()).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();*/


    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // ✅ React URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

/*    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails user1 = User
                .withDefaultPasswordEncoder()
                .username("kiran")
                .password("k@123")
                .roles("USER")
                .build();

        UserDetails user2 = User
                .withDefaultPasswordEncoder()
                .username("harsh")
                .password("h@123")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }*/

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }


}