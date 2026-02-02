package com.alt.card.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Card associated with an account (account_id). Persisted in card_service_db.
 */
@Entity
@Table(
        name = "card",
        indexes = {
                @Index(name = "idx_card_card_id", columnList = "card_id", unique = true),
                @Index(name = "idx_card_account_id", columnList = "account_id"),
                @Index(name = "idx_card_status", columnList = "status")
        }
)
public class Card extends AuditableEntity {

    @NotBlank
    @Size(max = 64)
    @Column(name = "card_id", nullable = false, unique = true, length = 64)
    public String cardId;

    @NotBlank
    @Size(max = 64)
    @Column(name = "account_id", nullable = false, length = 64)
    public String accountId;

    @Size(max = 20)
    @Column(name = "number", length = 20)
    public String number;

    @NotBlank
    @Size(max = 24)
    @Column(name = "masked_number", nullable = false, length = 24)
    public String maskedNumber;

    @Size(max = 32)
    @Column(name = "brand", length = 32)
    public String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    public CardType type = CardType.PHYSICAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    public CardStatus status = CardStatus.ACTIVE;
}
