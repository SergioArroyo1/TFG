package com.example.TFG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

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
                )

                // HEADERS DE SEGURIDAD (AÑADIDO SOLO ESTO)
                .headers(headers -> headers

                        // evita clickjacking
                        .frameOptions(frame -> frame.deny())

                        // CSP básica
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://unpkg.com; " +
                                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                                                "img-src 'self' data:; " +
                                                "font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                                                "connect-src 'self';"
                                )
                        )

                        // evita fuga de información en navegación
                        .referrerPolicy(referrer ->
                                referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )
                );

        return http.build();
    }
}