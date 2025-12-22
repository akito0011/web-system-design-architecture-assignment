package it.unipa.progettowsda.config;

import it.unipa.progettowsda.security.CustomAuthenticationSuccessHandler; // Assicurati di avere questo import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. DICHIARIAMO LA VARIABILE QUI
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // 2. LA INIETTIAMO TRAMITE IL COSTRUTTORE
    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Rotte per l'Admin
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // Rotte per lo Staff (AGGIUNTO)
                        .requestMatchers("/staff/**").hasAuthority("ROLE_STAFF")

                        // Rotte per il Cliente
                        .requestMatchers("/cliente/**").hasAuthority("ROLE_CLIENTE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // 3. ORA QUESTA VARIABILE NON SARÀ PIÙ ROSSA
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}