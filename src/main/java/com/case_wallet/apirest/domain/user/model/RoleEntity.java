package com.case_wallet.apirest.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    private Integer id;

    private RoleName name;

    public enum RoleName {
        USER,
        ADMIN
    }
} 