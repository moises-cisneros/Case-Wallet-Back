package com.case_wallet.apirest.infrastructure.security.config;

import com.case_wallet.apirest.common.exception.ApiError;
import com.case_wallet.apirest.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints administrativos
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Endpoints de KYC
                        .requestMatchers("/api/v1/kyc/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/kyc/**").authenticated()

                        // Endpoints de wallet
                        .requestMatchers("/api/v1/wallet/**").authenticated()

                        // Por defecto, requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ApiError apiError = ApiError.builder()
                                    .status(HttpStatus.UNAUTHORIZED.value())
                                    .error("No autorizado")
                                    .message(authException.getMessage())
                                    .path(request.getRequestURI())
                                    .timestamp(LocalDateTime.now())
                                    .build();

                            objectMapper.writeValue(response.getWriter(), apiError);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ApiError apiError = ApiError.builder()
                                    .status(HttpStatus.FORBIDDEN.value())
                                    .error("Acceso denegado")
                                    .message("No tienes los permisos necesarios para acceder a este recurso")
                                    .path(request.getRequestURI())
                                    .timestamp(LocalDateTime.now())
                                    .build();

                            objectMapper.writeValue(response.getWriter(), apiError);
                        })
                )
                .build();
    }
}