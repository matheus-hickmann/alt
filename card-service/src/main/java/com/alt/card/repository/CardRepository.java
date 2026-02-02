package com.alt.card.repository;

import com.alt.card.entity.Card;
import com.alt.card.entity.CardStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {

    public Optional<Card> findByCardId(String cardId) {
        return find("cardId", cardId).firstResultOptional();
    }

    public List<Card> findByAccountIdAndStatus(String accountId, CardStatus status) {
        return list("accountId = ?1 and status = ?2", accountId, status);
    }

    public List<Card> findByAccountId(String accountId) {
        return list("accountId", accountId);
    }
}
