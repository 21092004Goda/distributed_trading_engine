package org.kuro.repository;

import org.kuro.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<UserEntity> findByStatus(String status);

    List<UserEntity> findByUsernameContainingIgnoreCase(String username);

    List<UserEntity> findByEmailContaining(String email);


    @Query("SELECT u FROM UserEntity u WHERE u.status = :status ORDER BY u.createdAt DESC")
    List<UserEntity> findUsersByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status = :status")
    Long countByStatus(String status);

    @Query("SELECT u FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<UserEntity> findUsersByCreatedAtBetween(Timestamp startDate, Timestamp endDate);


    List<UserEntity> findByCreatedAtAfter(Timestamp date);

    List<UserEntity> findByCreatedAtBefore(Timestamp date);


    @Query("SELECT u FROM UserEntity u WHERE u.status IN :statuses")
    List<UserEntity> findUsersByStatuses(List<String> statuses);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.status = :status")
    Optional<UserEntity> findByEmailAndStatus(String email, String status);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.status = :status")
    Optional<UserEntity> findByUsernameAndStatus(String username, String status);

    @Query("SELECT u FROM UserEntity u ORDER BY u.createdAt DESC LIMIT :limit")
    List<UserEntity> findRecentUsers(int limit);

    @Query("SELECT u FROM UserEntity u WHERE u.updatedAt > :date")
    List<UserEntity> findUsersUpdatedAfter(Timestamp date);
}