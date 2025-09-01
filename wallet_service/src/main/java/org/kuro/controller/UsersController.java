package org.kuro.controller;


import jakarta.validation.Valid;
import org.kuro.model.dto.request.web.user.UserCreateRequest;
import org.kuro.model.dto.request.web.user.UserUpdateRequest;
import org.kuro.model.dto.response.web.user.UserCreateResponse;
import org.kuro.model.dto.response.web.user.UserResponse;
import org.kuro.model.dto.response.web.user.UserStatsResponse;
import org.kuro.repository.UsersRepository;
import org.kuro.service.UsersService;
import org.kuro.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortedBy
    ) {
        return ResponseEntity.ok(Page.empty());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(new UserResponse());
    }

    @PatchMapping("users/{id}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(new UserResponse());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/search/by-username")
    public ResponseEntity<List<UserResponse>> getUsersByUsername(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/users/search/by-email")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/users/filter/by-status")
    public ResponseEntity<Page<UserResponse>> getUsersByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(Page.empty());
    }

    @GetMapping("/users/filter/by-creation-date")
    public ResponseEntity<List<UserResponse>> getUsersByCreationDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp endDate
    ) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam String newStatus
    ) {
        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/users/check/exists")
    public ResponseEntity<Boolean> checkUserExists(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/users/stats")
    public ResponseEntity<UserStatsResponse> getUserStatistics() {
        return ResponseEntity.ok(new UserStatsResponse());
    }

    @PostMapping("/users/batch")
    public ResponseEntity<List<UserResponse>> createUsersBatch(
            @Valid @RequestBody List<UserCreateRequest> requests
    ) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @DeleteMapping("/users/batch")
    public ResponseEntity<Void> deleteUsersBatch(
            @RequestBody List<UUID> userIds
    ) {
        return ResponseEntity.ok(null);
    }
}
