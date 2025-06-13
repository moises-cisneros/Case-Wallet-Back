package com.case_wallet.apirest.infrastructure.database.user.adapter;

import com.case_wallet.apirest.application.user.mapper.UserMapper;
import com.case_wallet.apirest.application.user.port.out.UserRepositoryPort;
import com.case_wallet.apirest.domain.user.model.User;
import com.case_wallet.apirest.infrastructure.database.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryPortImpl implements UserRepositoryPort {

    private final JpaUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toModel);
    }

    @Override
    public User save(User user) {
        return userMapper.toModel(
                userRepository.save(
                        userMapper.toEntity(user)
                )
        );
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(userMapper::toModel);
    }

    // Métodos heredados que ya no se usarán pero deben mantenerse por compatibilidad
    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID ownerId) {
        return userRepository.existsById(ownerId);
    }
}