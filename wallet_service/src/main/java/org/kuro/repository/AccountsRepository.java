package org.kuro.repository;

import org.kuro.model.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountsRepository extends JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {
}
