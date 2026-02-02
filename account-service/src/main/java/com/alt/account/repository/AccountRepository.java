package com.alt.account.repository;

import com.alt.account.entity.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Repository for Account entity (db-accounts).
 */
@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {

    public Optional<Account> findByAccountId(String accountId) {
        return find("accountId", accountId).firstResultOptional();
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return find("accountNumber", accountNumber).firstResultOptional();
    }

    public boolean existsByDocument(String document) {
        return count("document", document) > 0;
    }
}
