package org.kuro.service;

import org.kuro.model.entity.TransactionEntity;
import org.kuro.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionsService {

    private final TransactionsRepository repository;

    @Autowired
    public TransactionsService(TransactionsRepository repository) {
        this.repository = repository;
    }

}
