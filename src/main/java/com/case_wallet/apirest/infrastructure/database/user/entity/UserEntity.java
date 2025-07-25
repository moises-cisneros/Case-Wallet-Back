package com.case_wallet.apirest.infrastructure.database.user.entity;

import com.case_wallet.apirest.domain.user.model.KYCStatus;
import com.case_wallet.apirest.domain.user.model.UserState;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String name;

    @Column(unique = true)
    private String googleId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "kyc_status")
    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus = KYCStatus.PENDING;

    @Column(name = "mantle_address", length = 42)
    private String mantleAddress;

    @Enumerated(EnumType.STRING)
    private UserState userState;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<com.case_wallet.apirest.infrastructure.database.user.entity.RoleEntity> roles = new HashSet<>();

    private Boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity(UUID userId) {
        this.id = userId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (roles.isEmpty()) {
            // Assign default role if no roles are set
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return passwordHash != null ? passwordHash : ""; // For OAuth users without password
    }

    @Override
    public String getUsername() {
        // Priority: phone number > email > googleId
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return phoneNumber;
        }
        if (email != null && !email.isEmpty()) {
            return email;
        }
        return googleId;
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
        return enabled;
    }
}
