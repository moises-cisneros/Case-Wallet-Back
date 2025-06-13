package com.case_wallet.apirest.domain.auth.model;

import com.case_wallet.apirest.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    private String token;
    private User user;
}
