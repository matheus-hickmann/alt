-- Update account status enum values from Portuguese to English
UPDATE account SET status = 'ACTIVE' WHERE status = 'ATIVA';
UPDATE account SET status = 'CANCELLED' WHERE status = 'CANCELADA';
