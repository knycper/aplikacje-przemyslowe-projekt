package com.example.demo.security;

import com.example.demo.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                        restAuthenticationEntryPoint(),
                        new AntPathRequestMatcher("/api/**")
                )
        );

        // Reguły dostępu
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // rejestracja i logowanie — oczywiście muszą być publiczne
                .requestMatchers(
                        "/api/auth/register",
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/h2-console/**"
                ).permitAll()

                // WSZYSTKIE endpointy API wymagają logowania
                .requestMatchers("/api/**").authenticated()

                // Wszystko inne też wymaga użytkownika
                .anyRequest().authenticated()
        );

        http.userDetailsService(userDetailsService);

        http.formLogin(login -> login
                .loginPage("/login")               // GET /login -> strona logowania
                .loginProcessingUrl("/login")      // POST /login -> obsługa logowania
                .defaultSuccessUrl("/tasks", true) // po logowaniu idzie na /
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "Authentication is required to access this API"
            }
            """);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}