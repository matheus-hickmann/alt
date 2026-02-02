package com.alt.card.mapper;

import com.alt.card.entity.Card;
import com.alt.card.entity.CardType;
import com.alt.proto.card.CardSummaryRpc;
import com.alt.proto.card.ListCardsByAccountIdRpcResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps Card entities to RPC response messages.
 */
@ApplicationScoped
public class CardRpcMapper {

    public CardSummaryRpc toCardSummaryRpc(Card card) {
        return CardSummaryRpc.newBuilder()
                .setId(card.cardId)
                .setMaskedNumber(card.maskedNumber)
                .setBrand(card.brand != null ? card.brand : "")
                .setType(card.type != null ? card.type.name() : CardType.PHYSICAL.name())
                .build();
    }

    public ListCardsByAccountIdRpcResponse toListCardsResponse(List<Card> cards) {
        List<CardSummaryRpc> summaries = cards.stream()
                .map(this::toCardSummaryRpc)
                .collect(Collectors.toList());
        return ListCardsByAccountIdRpcResponse.newBuilder()
                .addAllCards(summaries)
                .build();
    }
}
