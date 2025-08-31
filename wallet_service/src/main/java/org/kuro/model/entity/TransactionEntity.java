package org.kuro.model.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.kuro.model.type.TransactionType;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false)
    private UUID transactionId;

    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name = "debit_account_id")
    private AccountEntity debitAccount;

    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name = "credit_account_id")
    private AccountEntity creditAccount;

    @Column(name = "status")
    private String status;

    @Column(name = "related_object_id")
    private String relatedObjectId;

    @Column(name = "related_object_type")
    private String relatedObjectType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
