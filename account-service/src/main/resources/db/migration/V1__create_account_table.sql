-- Account table for account_service_db (db-accounts)
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL,
    document VARCHAR(14) NOT NULL,
    phone VARCHAR(20) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_account_account_id UNIQUE (account_id),
    CONSTRAINT uk_account_account_number UNIQUE (account_number)
);

CREATE INDEX idx_account_document ON account (document);
CREATE INDEX idx_account_status ON account (status);
