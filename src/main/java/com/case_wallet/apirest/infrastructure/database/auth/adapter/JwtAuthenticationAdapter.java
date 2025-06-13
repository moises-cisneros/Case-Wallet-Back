package com.case_wallet.apirest.infrastructure.database.auth.adapter;

import com.case_wallet.apirest.application.auth.port.out.AuthenticationPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.user.mapper.UserEntityMapper;
import com.case_wallet.apirest.infrastructure.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class JwtAuthenticationAdapter implements AuthenticationPort {
    private final JwtService jwtService;
    private final UserEntityMapper userEntityMapper;

    @Override
    public String generateToken(User user) {
        return jwtService.getToken(userEntityMapper.toNewEntity(user));
    }

    @Override
    public String getPhoneNumberFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims extractedClaims = jwtService.extractClaim(token, claims -> claims);
            return extractedClaims != null && !jwtService.isTokenValid(token, userEntityMapper.toNewEntity(
                User.builder()
                    .phoneNumber(extractedClaims.getSubject())
                    .build()
            ));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean validateToken(String token, User user) {
        return jwtService.isTokenValid(token, userEntityMapper.toNewEntity(user));
    }
}
