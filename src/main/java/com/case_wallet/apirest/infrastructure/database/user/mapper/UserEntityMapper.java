package com.case_wallet.apirest.infrastructure.database.user.mapper;

import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEntityMapper {
    public UserEntity toNewEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setPassword(entity.getPassword());
        user.setRole(entity.getRole());
        return user;
    }
}