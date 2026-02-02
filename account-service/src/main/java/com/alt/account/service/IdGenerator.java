package com.alt.account.service;

import com.alt.account.config.AccountConstants;
import com.alt.account.repository.AccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique identifiers for accounts.
 */
@ApplicationScoped
public class IdGenerator {

    private final AccountRepository accountRepository;

    public IdGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generateAccountId() {
        return AccountConstants.ACCOUNT_ID_PREFIX
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public String generateUniqueAccountNumber() {
        for (int i = 0; i < AccountConstants.UNIQUE_NUMBER_MAX_ATTEMPTS; i++) {
            String candidate = String.format(
                    "%0" + AccountConstants.ACCOUNT_NUMBER_LENGTH + "d",
                    ThreadLocalRandom.current().nextInt(
                            AccountConstants.ACCOUNT_NUMBER_MIN,
                            AccountConstants.ACCOUNT_NUMBER_MAX
                    )
            );
            if (accountRepository.findByAccountNumber(candidate).isEmpty()) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate unique account number");
    }
}
