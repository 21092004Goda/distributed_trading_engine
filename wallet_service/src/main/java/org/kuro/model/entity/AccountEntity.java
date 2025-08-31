package org.kuro.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false)
    private UUID accountId;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "currency")
    private String currency;

    @Column(name = "available_balance")
    private BigDecimal availableBalance;

    @Column(name = "reserved_balance")
    private BigDecimal reservedBalance;

    @Column(name = "total_balance")
    private BigDecimal totalBalance;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
