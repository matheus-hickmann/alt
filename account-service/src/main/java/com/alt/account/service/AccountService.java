package com.alt.account.service;

import com.alt.account.client.CardClient;
import com.alt.account.entity.Account;
import com.alt.account.entity.AccountStatus;
import com.alt.account.exception.AccountNotFoundException;
import com.alt.account.exception.InvalidAccountRequestException;
import com.alt.account.mapper.AccountRpcMapper;
import com.alt.account.messaging.CardEventsProducer;
import com.alt.account.repository.AccountRepository;
import com.alt.proto.account.*;
import com.alt.proto.card.*;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service for account operations: create (with physical card via Kafka),
 * cancel (validates account_id), getAccount (cached).
 */
@ApplicationScoped
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final AccountRpcMapper accountRpcMapper;
    private final CardClient cardClient;
    private final CardEventsProducer cardEventsProducer;

    public AccountService(AccountRepository accountRepository,
                          AccountFactory accountFactory,
                          AccountRpcMapper accountRpcMapper,
                          CardClient cardClient,
                          CardEventsProducer cardEventsProducer) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
        this.accountRpcMapper = accountRpcMapper;
        this.cardClient = cardClient;
        this.cardEventsProducer = cardEventsProducer;
    }

    @Transactional
    public CreateAccountRpcResponse createAccount(CreateAccountRpcRequest request) {
        Account account = accountFactory.createFromRequest(request);
        account.persist();

        cardEventsProducer.requestCreatePhysicalCard(account.accountId);

        return CreateAccountRpcResponse.newBuilder()
                .setAccountNumber(account.accountNumber)
                .setCardNumber("")
                .build();
    }

    @Transactional
    public CancelAccountRpcResponse cancelAccount(CancelAccountRpcRequest request) {
        String requestedAccountId = validateAccountId(request.getAccountId());

        Account account = accountRepository.findByAccountId(requestedAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + requestedAccountId));

        cardEventsProducer.requestCancelCardsByAccountId(requestedAccountId);

        account.status = AccountStatus.CANCELLED;
        account.updatedAt = java.time.Instant.now();
        account.persist();

        invalidateGetAccountCache(requestedAccountId);

        return CancelAccountRpcResponse.newBuilder().build();
    }

    @CacheInvalidate(cacheName = "get-account")
    public void invalidateGetAccountCache(String accountId) {
        // Cache key is accountId; annotation handles invalidation
    }

    public Optional<GetAccountRpcResponse> getAccount(GetAccountRpcRequest request) {
        GetAccountCacheEntry entry = getAccountCached(request.getAccountId());
        if (!entry.found() || entry.value() == null) {
            return Optional.empty();
        }
        return Optional.of(accountRpcMapper.toGetAccountRpcResponse(entry.value()));
    }

    @CacheResult(cacheName = "get-account")
    public GetAccountCacheEntry getAccountCached(String accountId) {
        Optional<Account> accountOpt = accountRepository.findByAccountId(accountId);
        if (accountOpt.isEmpty()) {
            return GetAccountCacheEntry.notFound();
        }
        Account account = accountOpt.get();
        ListCardsByAccountIdRpcResponse cardsResponse = cardClient.listCardsByAccountId(accountId);
        List<GetAccountCacheEntry.CardSummaryCache> cards = cardsResponse.getCardsList().stream()
                .map(c -> new GetAccountCacheEntry.CardSummaryCache(
                        c.getId(),
                        c.getMaskedNumber(),
                        c.getBrand(),
                        c.getType()))
                .collect(Collectors.toList());
        GetAccountCacheEntry.GetAccountCacheValue value = new GetAccountCacheEntry.GetAccountCacheValue(
                account.accountId,
                account.accountNumber,
                account.name,
                account.email,
                account.document,
                account.status.name(),
                cards);
        return GetAccountCacheEntry.of(value);
    }

    public Optional<Account> findByAccountId(String accountId) {
        return accountRepository.findByAccountId(accountId);
    }

    private static String validateAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new InvalidAccountRequestException("account_id is required");
        }
        return accountId;
    }
}
