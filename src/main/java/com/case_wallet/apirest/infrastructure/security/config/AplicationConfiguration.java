package com.case_wallet.apirest.infrastructure.security.config;

import com.case_wallet.apirest.infrastructure.database.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AplicationConfiguration {

    private final JpaUserRepository jpaUserRepository;

    @Bean
    // Define un Bean que proporciona un AuthenticationManager personalizado.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Proporciona un UserDetailsService personalizado que soporta tanto login por telefono como por email.
        return username -> {
            // Primero intenta buscar por número de teléfono
            return jpaUserRepository.findByPhoneNumber(username)
                    .or(() -> jpaUserRepository.findByEmail(username))
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Utiliza BCryptPasswordEncoder para el almacenamiento seguro de contraseñas.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Crea una instancia de DaoAuthenticationProvider, que se utiliza para autenticar usuarios.
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // Configura el UserDetailsService que se utiliza para cargar detalles del usuario durante la autenticacion.
        authenticationProvider.setUserDetailsService(userDetailsService());

        // Configura el PasswordEncoder que se utiliza para verificar contraseñas durante la autenticacion.
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }
}