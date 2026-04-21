package com.example.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // públicos
                        .requestMatchers("/", "/usuario/**", "/css/**", "/js/**").permitAll()

                        // admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // módulos de la app
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/finanzas/**").authenticated()
                        .requestMatchers("/eventos/**").authenticated()
                        .requestMatchers("/tareas/**").authenticated()
                        .requestMatchers("/habitos/**").authenticated()

                        // todo lo demás
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/usuario/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/usuario/login?logout")
                );

        return http.build();
    }
}