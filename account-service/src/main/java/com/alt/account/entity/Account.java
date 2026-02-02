package com.alt.account.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity representing an account in db-accounts (account_service_db).
 */
@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_account_id", columnList = "account_id", unique = true),
                @Index(name = "idx_account_account_number", columnList = "account_number", unique = true),
                @Index(name = "idx_account_document", columnList = "document"),
                @Index(name = "idx_account_status", columnList = "status")
        }
)
public class Account extends AuditableEntity {

    /**
     * External account identifier (used in API/gRPC), e.g. "acc_abc12345".
     */
    @NotBlank
    @Size(max = 64)
    @Column(name = "account_id", nullable = false, unique = true, length = 64)
    public String accountId;

    /**
     * Account number (e.g. 8 digits), e.g. "12345678".
     */
    @NotBlank
    @Size(min = 8, max = 20)
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    public String accountNumber;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    public String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    public String email;

    @NotBlank
    @Size(min = 11, max = 14)
    @Column(nullable = false, length = 14)
    public String document;

    @Size(max = 20)
    @Column(length = 20)
    public String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public AccountStatus status = AccountStatus.ACTIVE;
}
