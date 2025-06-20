package com.case_wallet.apirest.application.user.mapper;

import com.case_wallet.apirest.application.user.dto.UpdateUserProfileDTO;
import com.case_wallet.apirest.application.user.dto.UserProfileDTO;
import com.case_wallet.apirest.domain.user.model.Role;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import com.case_wallet.apirest.infrastructure.database.user.repository.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final JpaRoleRepository jpaRoleRepository;

    public UserProfileDTO toUserProfileDTO(User user) {
        if (user == null) return null;

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(roleNames)
                .kycStatus(user.getKycStatus())
                .userState(user.getUserState())
                .mantleAddress(user.getMantleAddress())
                .build();
    }

    public User toUser(UserProfileDTO dto) {
        if (dto == null) return null;

        Set<Role> roles = dto.getRoles().stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .roles(roles)
                .kycStatus(dto.getKycStatus())
                .userState(dto.getUserState())
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

        if (dto.getEmail() != null) existingUser.setEmail(dto.getEmail());
        
        return existingUser;
    }

    public User toModel(UserEntity entity) {
        if (entity == null) return null;

        Set<Role> roles = entity.getRoles().stream()
                .map(roleEntity -> Role.valueOf(roleEntity.getName()))
                .collect(Collectors.toSet());

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .googleId(entity.getGoogleId())
                .roles(roles)
                .kycStatus(entity.getKycStatus())
                .userState(entity.getUserState())
                .mantleAddress(entity.getMantleAddress())
                .enabled(entity.getEnabled())
                .build();
    }

    public UserEntity toEntity(User model) {
        if (model == null) return null;

        Set<RoleEntity> roleEntities = model.getRoles().stream()
                .map(role -> jpaRoleRepository.findByName(role.name())
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + role.name())))
                .collect(Collectors.toSet());

        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setEmail(model.getEmail());
        entity.setName(model.getName());
        entity.setGoogleId(model.getGoogleId());
        entity.setRoles(roleEntities);
        entity.setKycStatus(model.getKycStatus());
        entity.setUserState(model.getUserState());
        entity.setMantleAddress(model.getMantleAddress());
        entity.setEnabled(model.getEnabled());

        return entity;
    }

    public Set<Role> toDomainRoles(Set<RoleEntity> roleEntities) {
        if (roleEntities == null) {
            return Collections.emptySet();
        }
        return roleEntities.stream()
                .map(roleEntity -> Role.valueOf(roleEntity.getName()))
                .collect(Collectors.toSet());
    }
}
