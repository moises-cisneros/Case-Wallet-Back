package com.case_wallet.apirest.application.auth.usecases;

import com.case_wallet.apirest.application.auth.dto.AuthResponseDTO;
import com.case_wallet.apirest.application.auth.dto.LoginRequestDTO;
import com.case_wallet.apirest.application.auth.googleservice.GoogleTokenVerifierService;
import com.case_wallet.apirest.application.auth.port.in.AuthenticateUserUseCase;
import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.common.exception.ResourceNotFoundException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final AuthenticationPort authenticationPort;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    @Override
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        GoogleIdToken.Payload payload;
        try {
            payload = googleTokenVerifierService.verifyToken(request.getGoogleIdToken());
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Invalid Google ID Token", e);
        }

        if (payload == null) {
            throw new IllegalArgumentException("Invalid Google ID Token");
        }

        String email = payload.getEmail();
        String googleId = payload.getSubject();

        Optional<User> userOptional = userRepositoryPort.findByGoogleId(googleId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // You might want to update user details here if they change in Google
        } else {
            // If user doesn't exist, create a new one (similar to registration)
            // This flow might depend on your requirements - allow login for unregistered Google users or not.
            // For now, throwing an error if not found. You might change this to call register user use case.
            throw new ResourceNotFoundException("User not registered with this Google ID: " + googleId);
        }

        // The UsernamePasswordAuthenticationToken is still needed for Spring Security context,
        // but the password field is effectively empty/unused for OAuth2.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, // Use email as username for authentication
                        "" // Password is not used for Google OAuth
                )
        );

        String token = authenticationPort.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
