package org.kuro.controller;


import jakarta.validation.Valid;
import org.kuro.model.dto.request.web.user.UserCreateRequest;
import org.kuro.model.dto.response.web.user.UserCreateResponse;
import org.kuro.repository.UsersRepository;
import org.kuro.service.UsersService;
import org.kuro.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kuro")
public class UsersController {

    private final UsersService userService;
    private final UsersRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersController(
            UsersRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            UsersService userService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<UserCreateResponse> createUser(
            @Valid @RequestBody UserCreateRequest request
    ) {

        UserCreateResponse response = userService.createUser(request);

        return ResponseEntity.ok(response);
    }
}
