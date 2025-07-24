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
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .map(userMapper::toModel);
    }

    @Override
    public boolean existsByGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    @Override
    public boolean existsById(UUID ownerId) {
        return userRepository.existsById(ownerId);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(userMapper::toModel);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}