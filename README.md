# HookRelay

A production-grade webhook delivery service that guarantees at-least-once delivery
of events to registered endpoints, with automatic retries, HMAC-SHA256 signature
verification, and a dead letter queue for failed deliveries.

Built in Java 25 with Spring Boot, PostgreSQL, Docker and Redis.

---

## Overview

HookRelay sits between an event source and a merchant:

```
Event Source      →      HookRelay      →      Merchant Server
(your system)            (this service)         (yourapp.com/webhook)
                               ↓
                         stores event first
                               ↓
                         attempts delivery
                               ↓ fails
                         waits, retries
                               ↓ succeeds
                         marks delivered
```

The merchant's server will go down. HookRelay will not lose events.

---

## Features

- Merchant registration with API key authentication
- Endpoint registration with secret-based signature verification
- Event ingestion with immediate 202 response
- Background delivery worker using Java 21 virtual threads
- HMAC-SHA256 payload signing on every delivery
- Exponential backoff retry schedule across 6 attempts
- Dead letter queue for permanently failed deliveries
- Replay and dismiss endpoints for dead letter management
- Full delivery attempt history per event
- Mock merchant server for failure simulation testing

---

## Architecture

```
POST /api/events
      ↓
Save Event to PostgreSQL
      ↓
Find subscribed active endpoints
      ↓
Save Delivery per endpoint (status: QUEUED)
      ↓
Push delivery ID to Redis queue
      ↓
Return 202 immediately

Background Worker (every 10 seconds):
      ↓
Pop delivery ID from Redis
      ↓
Load delivery from PostgreSQL
      ↓
Sign payload with HMAC-SHA256
      ↓
POST to merchant endpoint URL
      ↓ 2xx  → mark DELIVERED, log attempt
      ↓ fail → mark FAILED, schedule retry
      ↓ 6 failures → DEAD_LETTER
```

---

## Tech Stack

```
Java 25             ← virtual threads for concurrent delivery
Spring Boot 3       ← REST API and background worker
PostgreSQL          ← persistent storage for all records
Redis               ← job queue for delivery workers
Docker Compose      ← runs PostgreSQL and Redis together
H2 (mock merchant)  ← in-memory DB for mock merchant server
```

---

## Getting Started

### Prerequisites

```
Java 25+
Docker Desktop
Maven
```

### 1. Clone the repository

```bash
git clone https://github.com/Richswag009/rookrelay.git
cd rookrelay
```

### 2. Start PostgreSQL and Redis

```bash
docker compose up -d
```

Verify containers are running:

```bash
docker ps
```

You should see:

```
hookrelay-postgres   Up   0.0.0.0:5432->5432/tcp
hookrelay-redis      Up   0.0.0.0:6379->6379/tcp
```

### 3. Configure environment

Create `.env` in the project root:

```
POSTGRES_DB=hookrelay
POSTGRES_USER=hookrelay
POSTGRES_PASSWORD=hookrelay
```

### 4. Run HookRelay

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`

### 5. Run Mock Merchant (optional, for testing)

```bash
cd mock-merchant
mvn spring-boot:run
```

Mock merchant starts on `http://localhost:8081`

---

## API Reference

All endpoints except `/api/merchants/register` and `/health` require:

```
X-API-KEY: hk_live_...
X-Merchant-ID: uuid...
```

---

### Merchant Registration

```
POST /api/merchants/register
```

Request:
```json
{
  "name": "Acme Payments",
  "email": "dev@acme.com",
  "phone": "08012345678"
}
```

Response `201`:
```json
{
  "id": "uuid",
  "name": "Acme Payments",
  "email": "dev@acme.com",
  "apiKey": "hk_live_abc123xyz"
}
```

Save your `apiKey` — it is shown once and never returned again.

---

### Endpoint Registration

```
POST /api/endpoints
```

Request:
```json
{
  "url": "https://yourapp.com/webhook",
  "description": "Payment notifications",
  "events": ["payment.success", "payment.failed"]
}
```

Response `201`:
```json
{
  "id": "ep_uuid",
  "url": "https://yourapp.com/webhook",
  "secret": "whsec_abc123",
  "events": ["payment.success", "payment.failed"],
  "status": "ACTIVE",
  "createdAt": "2026-01-01T00:00:00"
}
```

Save your `secret` — it is shown once. Use it to verify incoming webhook signatures.

---

### Event Ingestion

```
POST /api/events
```

Request:
```json
{
  "type": "payment.success",
  "payload": {
    "amount": 500000,
    "currency": "NGN",
    "reference": "TXN_001"
  }
}
```

Response `202`:
```json
{
  "id": "evt_uuid",
  "type": "payment.success",
  "status": "QUEUED",
  "createdAt": "2026-01-01T00:00:00"
}
```

HookRelay stores the event immediately and returns 202 without waiting for delivery.

---

### List Events

```
GET /api/events
```

Returns all events for the authenticated merchant.

---

### Delivery History

```
GET /api/events/{eventId}/deliveries
```

Response:
```json
[
  {
    "eventId": "evt_uuid",
    "endpointId": "ep_uuid",
    "deliveryStatus": "SUCCESSFUL",
    "attemptCount": 1,
    "attempts": [
      {
        "id": "attempt_uuid",
        "status": "SUCCESSFUL",
        "httpStatus": "200 OK",
        "responseBody": "OK",
        "attemptedAt": "2026-01-01T00:00:00",
        "nextRetryAt": null
      }
    ]
  }
]
```

---

### Dead Letter Queue

List failed deliveries:
```
GET /api/dead-letter
```

Replay a failed delivery:
```
POST /api/dead-letter/{deliveryId}/replay
```

Dismiss a failed delivery:
```
POST /api/dead-letter/{deliveryId}/dismiss
```

---

### Health Check

```
GET /health
```

No authentication required.

---

## Retry Schedule

```
Attempt 1 fails → wait 30 seconds
Attempt 2 fails → wait 5 minutes
Attempt 3 fails → wait 30 minutes
Attempt 4 fails → wait 2 hours
Attempt 5 fails → wait 5 hours
Attempt 6 fails → move to dead letter queue
```

---

## Payload Signing

Every delivery includes three headers:

```
X-Webhook-Signature: sha256=abc123...
X-Webhook-Timestamp: 1704067200
X-Webhook-ID: delivery-uuid
```

### How to verify in your application

```java
String signedContent = timestamp + "." + requestBody;
Mac hmac = Mac.getInstance("HmacSHA256");
hmac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
String expected = "sha256=" + HexFormat.of().formatHex(
        hmac.doFinal(signedContent.getBytes()));
boolean valid = expected.equals(receivedSignature);
```

Reject webhooks where:
- Signature does not match
- Timestamp is older than 5 minutes (replay attack prevention)

---

## Mock Merchant Server

A companion Spring Boot service that simulates a merchant receiving webhooks
with configurable failure rates for testing retry logic.

### Endpoints

```
POST /webhook   ← receives webhooks from HookRelay
GET  /received  ← returns all received webhooks
GET  /health    ← health check
```

### Configure failure behaviour

In `mock-merchant/src/main/resources/application.properties`:

```properties
merchant.failure.rate=0.4    # 40% of requests return 500
merchant.timeout.rate=0.1    # 10% of requests hang for 60 seconds
merchant.slow.rate=0.2       # 20% of requests take 10 seconds
```

### Register mock merchant as endpoint in HookRelay

```json
{
  "url": "http://localhost:8081/webhook",
  "description": "Mock merchant for testing",
  "events": ["payment.success", "payment.failed"]
}
```

---

## Database Schema

```
merchants
    id, name, email, phone, api_key_hash, created_at

endpoints
    id, merchant_id, url, secret_hash, subscribed_events[],
    status, created_at

events
    id, merchant_id, type, payload, status, created_at

deliveries
    id, event_id, endpoint_id, status, attempt_count,
    next_retry_at, created_at

delivery_attempts
    id, delivery_id, attempt_number, http_status,
    response_body, attempted_at
```

---

## Project Structure

```
hookrelay/
├── README.md
├── TRADEOFFS.md
├── docker-compose.yml
├── .gitignore
├── pom.xml
├── mock-merchant/                     ← companion test server
│   └── src/main/java/
│       ├── WebhookController.java
│       ├── WebhookRecord.java
│       ├── WebhookRepository.java
│       └── FailureSimulator.java
└── src/main/java/com/richcodes/hookrelay/
    ├── api/                           ← REST controllers
    ├── domain/                        ← entities
    ├── worker/                        ← delivery worker + retry policy
    ├── signing/                       ← HMAC signature generation
    ├── config/                        ← API key filter, app config
    ├── repository/                    ← JPA repositories
    └── services/                      ← business logic
```

---

## What This Project Demonstrates

- API key authentication for machine-to-machine systems
- At-least-once delivery guarantee with Redis job queue
- Fault-tolerant background workers with Java 21 virtual threads
- HMAC-SHA256 webhook signing and replay attack prevention
- Exponential backoff retry with dead letter queue recovery
- Store-before-deliver pattern for zero event loss
- Docker-based local development environment

---

Built by [Riches Metelewawon](https://linkedin.com/in/richesmetelewawon)