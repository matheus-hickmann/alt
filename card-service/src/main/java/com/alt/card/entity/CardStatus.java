package com.alt.card.entity;

/**
 * Card states in physical flow: PENDING_DELIVERY → DELIVERED (webhook) → ACTIVE (activation).
 */
public enum CardStatus {
    PENDING_DELIVERY,
    DELIVERED,
    ACTIVE,
    CANCELLED
}
