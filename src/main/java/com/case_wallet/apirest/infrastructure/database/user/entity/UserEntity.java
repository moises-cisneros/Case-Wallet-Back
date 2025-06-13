package com.case_wallet.apirest.infrastructure.database.user.entity;

import com.case_wallet.apirest.domain.user.model.KYCStatus;
import com.case_wallet.apirest.domain.user.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", unique = true, nullable = false, length = 8)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(name = "kyc_status")
    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus = KYCStatus.PENDING;

    @Column(name = "mantle_address", length = 42)
    private String mantleAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255) default 'USER'")
    private Role role = Role.USER;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity(UUID userId) {
        this.id = userId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = Role.USER;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
