package com.case_wallet.apirest.application.user.usecases;

import com.case_wallet.apirest.application.user.dto.ChangePasswordDTO;
import com.case_wallet.apirest.application.user.port.in.ChangeUserPasswordUseCase;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeUserPasswordUseCaseImpl implements ChangeUserPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(UUID userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepositoryPort.save(user);
    }
}