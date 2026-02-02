CREATE TABLE card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id VARCHAR(64) NOT NULL,
    account_id VARCHAR(64) NOT NULL,
    number VARCHAR(20),
    masked_number VARCHAR(24) NOT NULL,
    brand VARCHAR(32),
    type VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_card_card_id UNIQUE (card_id)
);

CREATE INDEX idx_card_account_id ON card (account_id);
CREATE INDEX idx_card_status ON card (status);
