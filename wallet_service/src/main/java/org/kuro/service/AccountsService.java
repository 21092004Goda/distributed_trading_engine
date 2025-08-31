package org.kuro.service;


import org.kuro.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

    private final AccountsRepository repository;

    @Autowired
    public AccountsService(AccountsRepository repository) {
        this.repository = repository;
    }
}
