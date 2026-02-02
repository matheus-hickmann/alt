package com.alt.card.service;

import com.alt.card.entity.Card;
import com.alt.card.repository.CardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Test-only helper to run card persistence and cleanup inside a transaction.
 */
@ApplicationScoped
public class CardTestDataHelper {

    @Inject
    CardRepository cardRepository;

    @Transactional
    public void deleteAll() {
        cardRepository.deleteAll();
    }

    @Transactional
    public void persist(Card card) {
        card.persist();
    }
}
