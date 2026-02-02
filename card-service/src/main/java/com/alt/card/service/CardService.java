package com.alt.card.service;

import com.alt.card.entity.Card;
import com.alt.card.entity.CardStatus;
import com.alt.card.exception.CardNotFoundException;
import com.alt.card.exception.InvalidCardRequestException;
import com.alt.card.exception.InvalidCardStateException;
import com.alt.card.mapper.CardRpcMapper;
import com.alt.card.messaging.AccountCacheInvalidationProducer;
import com.alt.card.repository.CardRepository;
import com.alt.proto.card.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for card operations: create, list, cancel, activate.
 */
@ApplicationScoped
public class CardService {

    private final CardRepository cardRepository;
    private final CardFactory cardFactory;
    private final CardRpcMapper cardRpcMapper;
    private final AccountCacheInvalidationProducer accountCacheInvalidationProducer;

    public CardService(CardRepository cardRepository,
                       CardFactory cardFactory,
                       CardRpcMapper cardRpcMapper,
                       AccountCacheInvalidationProducer accountCacheInvalidationProducer) {
        this.cardRepository = cardRepository;
        this.cardFactory = cardFactory;
        this.cardRpcMapper = cardRpcMapper;
        this.accountCacheInvalidationProducer = accountCacheInvalidationProducer;
    }

    @Transactional
    public CreatePhysicalCardRpcResponse createPhysicalCard(CreatePhysicalCardRpcRequest request) {
        Card card = cardFactory.createPhysicalCard(request);
        card.persist();

        return CreatePhysicalCardRpcResponse.newBuilder()
                .setId(card.cardId)
                .setNumber(card.number)
                .build();
    }

    public ListCardsByAccountIdRpcResponse listCardsByAccountId(ListCardsByAccountIdRpcRequest request) {
        String accountId = request.getAccountId();
        if (isBlank(accountId)) {
            return ListCardsByAccountIdRpcResponse.newBuilder().build();
        }
        List<Card> cards = cardRepository.findByAccountId(accountId);
        List<CardSummaryRpc> summaries = cards.stream()
                .filter(c -> c.status == CardStatus.ACTIVE)
                .map(cardRpcMapper::toCardSummaryRpc)
                .collect(Collectors.toList());
        return ListCardsByAccountIdRpcResponse.newBuilder().addAllCards(summaries).build();
    }

    @Transactional
    public CancelCardsByAccountIdRpcResponse cancelCardsByAccountId(CancelCardsByAccountIdRpcRequest request) {
        String accountId = request.getAccountId();
        if (isBlank(accountId)) {
            return CancelCardsByAccountIdRpcResponse.newBuilder().build();
        }
        List<Card> cards = cardRepository.findByAccountId(accountId);
        for (Card card : cards) {
            if (card.status == CardStatus.ACTIVE) {
                card.status = CardStatus.CANCELLED;
                card.updatedAt = Instant.now();
                card.persist();
            }
        }
        return CancelCardsByAccountIdRpcResponse.newBuilder().build();
    }

    @Transactional
    public CancelCardRpcResponse cancelCard(CancelCardRpcRequest request) {
        String cardId = validateCardId(request.getCardId());
        Card card = findCardOrThrow(cardId);

        if (card.status == CardStatus.CANCELLED) {
            return CancelCardRpcResponse.newBuilder().build();
        }
        card.status = CardStatus.CANCELLED;
        card.updatedAt = Instant.now();
        card.persist();

        accountCacheInvalidationProducer.requestInvalidateAccountCache(card.accountId);

        return CancelCardRpcResponse.newBuilder().build();
    }

    @Transactional
    public ActivateCardRpcResponse activateCard(ActivateCardRpcRequest request) {
        String cardId = validateCardId(request.getCardId());
        Card card = findCardOrThrow(cardId);

        if (card.status != CardStatus.DELIVERED) {
            throw new InvalidCardStateException(
                    "Card can only be activated when status is DELIVERED. Current: " + card.status);
        }
        card.status = CardStatus.ACTIVE;
        card.updatedAt = Instant.now();
        card.persist();

        accountCacheInvalidationProducer.requestInvalidateAccountCache(card.accountId);

        return ActivateCardRpcResponse.newBuilder().build();
    }

    private Card findCardOrThrow(String cardId) {
        return cardRepository.findByCardId(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardId));
    }

    private static String validateCardId(String cardId) {
        if (cardId == null || cardId.isBlank()) {
            throw new InvalidCardRequestException("card_id is required");
        }
        return cardId;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
