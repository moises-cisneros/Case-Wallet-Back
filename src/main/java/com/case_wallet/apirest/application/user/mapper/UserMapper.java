package com.case_wallet.apirest.application.user.mapper;

import com.case_wallet.apirest.application.user.dto.UpdateUserProfileDTO;
import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserProfileDTO toUserProfileDTO(User user) {
        if (user == null) return null;

        return UserProfileDTO.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                //.password(dto.getPassword())
                //.pinHash(dto.getPinHash())
                .role(user.getRole())
                .kycStatus(user.getKycStatus())
                .mantleAddress(user.getMantleAddress())
                .build();
    }

    public User toUser(UserProfileDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .phoneNumber(dto.getPhoneNumber())
                //.password(dto.getPassword())
                //.pinHash(dto.getPinHash())
                .role(dto.getRole())
                .kycStatus(dto.getKycStatus())
                .mantleAddress(dto.getMantleAddress())
                .build();

    }

    public User updateUserFromDTO(UpdateUserProfileDTO dto, User existingUser) {
        if (existingUser == null) {
            return null;
        }
        if (dto == null) {
            return existingUser;
        }

        if (dto.getPhoneNumber() != null) existingUser.setPhoneNumber(dto.getPhoneNumber());
        
        return existingUser;
    }

    public User toModel(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .phoneNumber(entity.getPhoneNumber())
                .password(entity.getPassword())
                .pinHash(entity.getPinHash())
                .role(entity.getRole())
                .kycStatus(entity.getKycStatus())
                .mantleAddress(entity.getMantleAddress())
                .build();
    }

    public UserEntity toEntity(User model) {
        if (model == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setPhoneNumber(model.getPhoneNumber());
        entity.setPassword(model.getPassword());
        entity.setPinHash(model.getPinHash());
        entity.setRole(model.getRole());
        entity.setKycStatus(model.getKycStatus());
        entity.setMantleAddress(model.getMantleAddress());

        return entity;
    }
}
