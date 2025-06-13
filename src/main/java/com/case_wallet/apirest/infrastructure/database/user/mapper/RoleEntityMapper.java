package com.case_wallet.apirest.infrastructure.database.user.mapper;

import com.case_wallet.apirest.domain.user.model.Role;
import com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity;

public abstract class RoleEntityMapper {

    /*
    RoleEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        return entity;
    }

    Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());
        return role;
    }*/
}