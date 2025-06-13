package com.case_wallet.apirest.infrastructure.rest.user.controller;

import com.case_wallet.apirest.application.user.dto.ChangePasswordDTO;
import com.case_wallet.apirest.application.user.dto.UpdateUserProfileDTO;
import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import com.case_wallet.apirest.application.user.port.in.ChangeUserPasswordUseCase;
import com.case_wallet.apirest.application.user.port.in.GetUserProfileUseCase;
import com.case_wallet.apirest.application.user.port.in.UpdateUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String userId, HttpServletRequest request) {
        log.info("Requesting user profile - UserId: {} - IP: {} - URL: {}", 
            userId, 
            request.getRemoteAddr(), 
            request.getRequestURL()
        );
        
        try {
            UserProfileDTO profile = getUserProfileUseCase.getUserProfile(UUID.fromString(userId));
            log.info("User profile retrieved successfully - UserId: {}", userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Error retrieving user profile - UserId: {} - Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        return ResponseEntity.ok(updateUserProfileUseCase.updateProfile(updateUserProfileDTO));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String userId,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        changeUserPasswordUseCase.changePassword(UUID.fromString(userId), changePasswordDTO);
        return ResponseEntity.ok().build();
    }
}
