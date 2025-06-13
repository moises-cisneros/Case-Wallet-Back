package com.case_wallet.apirest.application.user.usecases;

import com.case_wallet.apirest.application.user.dto.UpdateUserProfileDTO;
import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import com.case_wallet.apirest.application.user.mapper.UserMapper;
import com.case_wallet.apirest.application.user.port.in.UpdateUserProfileUseCase;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    public UserProfileDTO updateProfile(UpdateUserProfileDTO updateUserProfileDTO) {
        User existingUser = userRepositoryPort.findById(updateUserProfileDTO.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        User updatedUser = userMapper.updateUserFromDTO(updateUserProfileDTO, existingUser);
        
        User savedUser = userRepositoryPort.save(updatedUser);
        return userMapper.toUserProfileDTO(savedUser);
    }
}