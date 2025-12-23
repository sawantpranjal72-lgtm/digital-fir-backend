package com.digitalfir.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // 🔓 PUBLIC
                .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                // 🔓 Evidence VIEW (public – browser open)
                .requestMatchers(HttpMethod.GET, "/api/evidence/view/**")
                    .permitAll()

                // 📤 UPLOAD EVIDENCE
                .requestMatchers(HttpMethod.POST, "/api/evidence/upload/**")
                    .hasAnyRole("CITIZEN", "POLICE", "ADMIN")

                // 📂 GET EVIDENCE BY FIR
                .requestMatchers(HttpMethod.GET, "/api/evidence/fir/**")
                    .hasAnyRole("CITIZEN", "POLICE", "ADMIN")

                // ⬇️ DOWNLOAD EVIDENCE
                .requestMatchers(HttpMethod.GET, "/api/evidence/download/**")
                    .hasAnyRole("CITIZEN", "POLICE", "ADMIN")

                // 🗑 DELETE EVIDENCE
                .requestMatchers(HttpMethod.DELETE, "/api/evidence/**")
                    .hasAnyRole("POLICE", "ADMIN")

                // 🟢 Citizen FIR create
                .requestMatchers(HttpMethod.POST, "/api/fir/create")
                    .hasRole("CITIZEN")

                // 🟡 FIR status update
                .requestMatchers(HttpMethod.PUT, "/api/fir/update-status/**")
                    .hasAnyRole("POLICE", "ADMIN")

                // 👀 FIR view
                .requestMatchers(HttpMethod.GET, "/api/fir/**")
                    .hasAnyRole("CITIZEN", "POLICE", "ADMIN")

                // 🗑 FIR delete
                .requestMatchers(HttpMethod.DELETE, "/api/fir/**")
                    .hasAnyRole("POLICE", "ADMIN")

                // बाकी सगळं secure
                .anyRequest().authenticated()
            )

            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

