## 📋 Полный CRUD + Дополнительные операции

### 1. **Создание пользователя**
```java
@PostMapping("/users")
public ResponseEntity<UserResponse> createUser(
    @Valid @RequestBody UserCreateRequest request
)
```
**Тело запроса:**
```json
{
    "username": "Elena_Orlova_92",
    "email": "elena.orlova92@example.com",
    "password": "SecurePass123!",
    "status": "ACTIVE"
}
```

### 2. **Получение пользователя по ID**
```java
@GetMapping("/users/{id}")
public ResponseEntity<UserResponse> getUserById(
    @PathVariable UUID id
)
```

### 3. **Получение всех пользователей (с пагинацией)**
```java
@GetMapping("/users")
public ResponseEntity<Page<UserResponse>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "createdAt") String sortBy
)
```

### 4. **Обновление пользователя**
```java
@PutMapping("/users/{id}")
public ResponseEntity<UserResponse> updateUser(
    @PathVariable UUID id,
    @Valid @RequestBody UserUpdateRequest request
)
```
**Тело запроса:**
```json
{
    "username": "NewUsername",
    "email": "new.email@example.com",
    "status": "SUSPENDED"
}
```

### 5. **Частичное обновление (PATCH)**
```java
@PatchMapping("/users/{id}")
public ResponseEntity<UserResponse> patchUser(
    @PathVariable UUID id,
    @RequestBody Map<String, Object> updates
)
```

### 6. **Удаление пользователя**
```java
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(
    @PathVariable UUID id
)
```

### 7. **Поиск пользователей по имени пользователя**
```java
@GetMapping("/users/search/by-username")
public ResponseEntity<List<UserResponse>> getUsersByUsername(
    @RequestParam String username
)
```

### 8. **Поиск пользователей по email**
```java
@GetMapping("/users/search/by-email")
public ResponseEntity<UserResponse> getUserByEmail(
    @RequestParam String email
)
```

### 9. **Фильтрация пользователей по статусу**
```java
@GetMapping("/users/filter/by-status")
public ResponseEntity<Page<UserResponse>> getUsersByStatus(
    @RequestParam String status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
)
```

### 10. **Получение пользователей, созданных в определенный период**
```java
@GetMapping("/users/filter/by-creation-date")
public ResponseEntity<List<UserResponse>> getUsersByCreationDate(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Timestamp endDate
)
```

### 11. **Изменение статуса пользователя**
```java
@PatchMapping("/users/{id}/status")
public ResponseEntity<UserResponse> updateUserStatus(
    @PathVariable UUID id,
    @RequestParam String newStatus
)
```

### 12. **Смена пароля пользователя**
```java
@PatchMapping("/users/{id}/password")
public ResponseEntity<Void> changePassword(
    @PathVariable UUID id,
    @RequestBody PasswordChangeRequest request
)
```
**Тело запроса:**
```json
{
    "oldPassword": "oldPassword123",
    "newPassword": "newSecurePassword456!"
}
```

### 13. **Сброс пароля (администратором)**
```java
@PostMapping("/users/{id}/reset-password")
public ResponseEntity<Void> resetPassword(
    @PathVariable UUID id
)
```

### 14. **Проверка существования пользователя**
```java
@GetMapping("/users/check/exists")
public ResponseEntity<Boolean> checkUserExists(
    @RequestParam(required = false) String username,
    @RequestParam(required = false) String email
)
```

### 15. **Получение статистики пользователей**
```java
@GetMapping("/users/stats")
public ResponseEntity<UserStatsResponse> getUserStatistics()
```
**Ответ:**
```json
{
    "totalUsers": 150,
    "activeUsers": 120,
    "pendingUsers": 15,
    "suspendedUsers": 15,
    "usersCreatedToday": 5
}
```

### 16. **Массовое создание пользователей**
```java
@PostMapping("/users/batch")
public ResponseEntity<List<UserResponse>> createUsersBatch(
    @Valid @RequestBody List<UserCreateRequest> requests
)
```

### 17. **Массовое удаление пользователей**
```java
@DeleteMapping("/users/batch")
public ResponseEntity<Void> deleteUsersBatch(
    @RequestBody List<UUID> userIds
)
```

### 18. **Экспорт пользователей в CSV/JSON**
```java
@GetMapping(value = "/users/export", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    "text/csv"
})
public ResponseEntity<?> exportUsers(
    @RequestParam(defaultValue = "json") String format,
    @RequestParam(required = false) String status
)
```

## 🛡️ Дополнительные security endpoints

### 19. **Получение текущего аутентифицированного пользователя**
```java
@GetMapping("/users/me")
public ResponseEntity<UserResponse> getCurrentUser(
    @AuthenticationPrincipal UserDetails userDetails
)
```

### 20. **Обновление профиля текущего пользователя**
```java
@PutMapping("/users/me")
public ResponseEntity<UserResponse> updateCurrentUser(
    @AuthenticationPrincipal UserDetails userDetails,
    @Valid @RequestBody UserUpdateRequest request
)
```

## 📊 DTO классы для запросов/ответов

```java
// UserCreateRequest.java
public record UserCreateRequest(
    @NotBlank String username,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8) String password,
    String status
) {}

// UserUpdateRequest.java
public record UserUpdateRequest(
    String username,
    @Email String email,
    String status
) {}

// UserResponse.java
public record UserResponse(
    UUID userId,
    String username,
    String email,
    String status,
    Timestamp createdAt,
    Timestamp updatedAt
) {}
```

## 🔐 Рекомендуемые права доступа

- **GET endpoints** - доступны для всех аутентифицированных пользователей
- **POST/PUT/PATCH/DELETE** - только для администраторов
- **/users/me** - доступ к собственному профилю

Такой набор endpoints обеспечит полное управление пользователями с возможностью масштабирования!