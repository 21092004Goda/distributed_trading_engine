package org.kuro.service;

import org.kuro.model.dto.request.web.user.UserCreateRequest;
import org.kuro.model.dto.response.web.user.UserCreateResponse;
import org.kuro.model.entity.UserEntity;
import org.kuro.repository.UsersRepository;
import org.kuro.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class UsersService {

    private final UsersRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(
            UsersRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserCreateResponse createUser(UserCreateRequest request) {

        UserEntity entity = userMapper.toEntity(request);

        entity.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        entity.setCreatedAt(currentTime);
        entity.setUpdatedAt(currentTime);

        UserEntity savedUser = userRepository.save(entity);

        return userMapper.toResponse(savedUser);
    }
}
