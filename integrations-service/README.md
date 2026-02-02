# Integrations Service

External integrations service for Alt Bank system.

## Features

### 1. Webhooks (Receiving)

Endpoints to receive notifications from external systems:

#### Physical Card Delivery
```bash
POST /webhooks/card-delivery
Header: X-API-Key: carrier-api-key-secret
Content-Type: application/json

{
  "tracking_id": "TRACK-12345",
  "delivery_status": "DELIVERED",
  "delivery_date": "2026-02-01T10:30:00",
  "delivery_return_reason": null,
  "delivery_address": "123 Main St"
}
```

#### CVV Change (Virtual Card)
```bash
POST /webhooks/card-cvv-change
Header: X-API-Key: processor-api-key-secret
Content-Type: application/json

{
  "account_id": "proc-acc-123",
  "card_id": "proc-card-456",
  "next_cvv": 456,
  "expiration_date": "2026-03-01T10:30:00"
}
```

### 2. Mock Endpoints (Simulation)

Endpoints to simulate external system calls during testing:

#### Mock: Successful Delivery
```bash
POST /mock/carrier/notify-delivery?trackingId=TRACK-12345&deliveryAddress=123%20Main%20St
```

#### Mock: Delivery Failed
```bash
POST /mock/carrier/notify-delivery-failed?trackingId=TRACK-12345&returnReason=Recipient%20absent
```

#### Mock: CVV Change
```bash
POST /mock/processor/notify-cvv-change?accountId=proc-acc-123&cardId=proc-card-456&nextCvv=456
```

### 3. gRPC Service

gRPC service for card-service to get full card data:

- **Port**: 9100
- **Service**: `CardDataService.GetFullCardData`

## Configuration

### API Keys

Configure API Keys in `application.properties`:

```properties
integrations.api-key.carrier=carrier-api-key-secret
integrations.api-key.processor=processor-api-key-secret
```

### Webhook URL

For local testing, the webhook base URL can be configured:

```properties
integrations.webhook.base-url=http://localhost:8081
```

## Authentication

All webhooks require authentication via `X-API-Key` header. The `ApiKeyFilter` automatically validates requests.

## Running

```bash
mvn quarkus:dev
```

The service will be available at:
- HTTP: http://localhost:8081
- gRPC: localhost:9100

## Test Examples

### Test delivery webhook with curl:
```bash
curl -X POST http://localhost:8081/webhooks/card-delivery \
  -H "Content-Type: application/json" \
  -H "X-API-Key: carrier-api-key-secret" \
  -d '{
    "tracking_id": "TRACK-12345",
    "delivery_status": "DELIVERED",
    "delivery_date": "2026-02-01T10:30:00",
    "delivery_return_reason": null,
    "delivery_address": "123 Main St"
  }'
```

### Test delivery mock:
```bash
curl -X POST "http://localhost:8081/mock/carrier/notify-delivery?trackingId=TRACK-99999&deliveryAddress=456%20Test%20St"
```

### Test CVV webhook with curl:
```bash
curl -X POST http://localhost:8081/webhooks/card-cvv-change \
  -H "Content-Type: application/json" \
  -H "X-API-Key: processor-api-key-secret" \
  -d '{
    "account_id": "proc-acc-123",
    "card_id": "proc-card-456",
    "next_cvv": 789,
    "expiration_date": "2026-03-01T10:30:00"
  }'
```

### Test CVV mock:
```bash
curl -X POST "http://localhost:8081/mock/processor/notify-cvv-change?accountId=proc-acc-999&cardId=proc-card-888&nextCvv=321"
```
