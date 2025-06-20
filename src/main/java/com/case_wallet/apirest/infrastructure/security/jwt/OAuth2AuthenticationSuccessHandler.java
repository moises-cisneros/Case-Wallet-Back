package com.case_wallet.apirest.infrastructure.security.jwt;

import com.case_wallet.apirest.infrastructure.security.config.HttpCookieOAuth2AuthorizationRequestRepository;
import com.case_wallet.apirest.infrastructure.security.service.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${frontend.oauth2.redirect-url}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {

            // Generate JWT token
            String jwt = jwtService.generateToken(customOAuth2User.getUserId(), customOAuth2User.getAuthorities());

            // Clear the authorization request cookie
            httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

            // Redirect to a frontend URL with the JWT token
            String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                    .queryParam("token", jwt)
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
} 