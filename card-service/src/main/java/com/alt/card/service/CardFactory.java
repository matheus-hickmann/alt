package com.alt.card.service;

import com.alt.card.config.CardConstants;
import com.alt.card.entity.Card;
import com.alt.card.entity.CardStatus;
import com.alt.card.entity.CardType;
import com.alt.proto.card.CreatePhysicalCardRpcRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

/**
 * Factory for creating Card entities.
 */
@ApplicationScoped
public class CardFactory {

    public Card createPhysicalCard(CreatePhysicalCardRpcRequest request) {
        String cardId = CardConstants.CARD_ID_PREFIX
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String number = CardNumberGenerator.generate();
        String maskedNumber = CardNumberGenerator.mask(number);

        Card card = new Card();
        card.cardId = cardId;
        card.accountId = request.getAccountId();
        card.number = number;
        card.maskedNumber = maskedNumber;
        card.brand = CardConstants.DEFAULT_BRAND;
        card.type = CardType.PHYSICAL;
        card.status = CardStatus.PENDING_DELIVERY;
        return card;
    }
}
