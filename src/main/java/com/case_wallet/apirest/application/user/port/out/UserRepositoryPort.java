package com.case_wallet.apirest.application.user.port.out;

import com.case_wallet.apirest.domain.user.model.User;

import java.util.UUID;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(UUID id);
    User save(User user);
    void deleteById(UUID id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsById(UUID ownerId);
    Optional<User> findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);
}