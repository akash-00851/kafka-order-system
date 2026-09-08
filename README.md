# Kafka Order Processing System

A small event-driven order processing system built with Spring Boot and
Apache Kafka. Placing an order triggers a chain of events across three
independent microservices: an order is created, stock is checked and
reserved, and a notification is sent — each step handled by a separate
service communicating through Kafka topics instead of direct API calls.

## Architecture

```
Order Service (MySQL)
      │
      │  POST /api/orders  →  saves order  →  publishes OrderCreatedEvent
      ▼
  topic: orders.created
      │
      ▼
Inventory Service (MongoDB)
      │
      │  checks + deducts stock  →  publishes InventoryStatusEvent
      ▼
  topic: inventory.status
      │
      ├──────────────┬──────────────────┐
      ▼                                  ▼
Order Service                    Notification Service (MongoDB)
(updates order to                (simulates sending an email,
 CONFIRMED / FAILED)              logs it)
```

## Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| order-service | 8081 | MySQL | REST API for placing and viewing orders. Produces `orders.created`, consumes `inventory.status`. |
| inventory-service | 8082 | MongoDB | Consumes `orders.created`, checks and deducts stock, produces `inventory.status`. |
| notification-service | 8083 | MongoDB | Consumes `inventory.status`, simulates sending a notification and logs it. |

Kafka, MySQL, MongoDB, and Kafka UI all run via Docker Compose.

## Tech stack

Java 17, Spring Boot, Spring MVC, Spring Data JPA, Spring Data MongoDB,
Spring Kafka, MySQL, MongoDB, Apache Kafka (KRaft mode), Docker Compose.

## Prerequisites

- Java 21
- Maven 3.8+
- Docker Desktop (running before you start anything)

## Setup

**1. Clone the repo and start the infrastructure**

```bash
docker compose up -d
```

Wait about 15–20 seconds for Kafka to fully come up before starting the
services. Confirm everything is running:

```bash
docker ps
```

You should see four containers: `kafka`, `kafka-ui`, `mysql`, `mongodb`.

**2. Start each service** (in separate terminals, from the project root)

```bash
cd order-service && mvn spring-boot:run
```
```bash
cd inventory-service && mvn spring-boot:run
```
```bash
cd notification-service && mvn spring-boot:run
```

Each should print `Started ...Application` once ready.

## API

### Order Service — `http://localhost:8081`

Swagger UI: `http://localhost:8081/swagger-ui.html`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders/{orderReference}` | Get a single order |
| GET | `/api/orders` | List all orders |

**Create an order:**
```json
POST /api/orders
{
  "customerName": "Akash",
  "customerEmail": "akash@example.com",
  "items": [
    { "productId": "P001", "productName": "Mechanical Keyboard", "quantity": 1, "price": 3499.0 },
    { "productId": "P002", "productName": "USB-C Hub", "quantity": 2, "price": 899.0 }
  ]
}
```

### Inventory Service — `http://localhost:8082`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Add or top up stock for a product |
| GET | `/api/products` | List all products |
| GET | `/api/products/{productId}` | Get a single product |

**Add stock:**
```json
POST /api/products
{ "productId": "P001", "productName": "Mechanical Keyboard", "quantity": 10 }
```

### Notification Service — `http://localhost:8083`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications` | List all notifications |
| GET | `/api/notifications/order/{orderReference}` | Get notifications for a specific order |

## Trying it out

1. Add stock for a couple of products via `POST /api/products`
2. Place an order via `POST /api/orders` — note the `orderReference` in the response
3. Check `GET /api/orders/{orderReference}` — status starts as `CREATED` and flips to `CONFIRMED` within a second or two, once the event chain completes
4. Check `GET /api/notifications/order/{orderReference}` to see the simulated notification
5. Try ordering more units than are in stock to see the failure path — the order ends up `FAILED` with a reason attached

You can also watch the messages move through Kafka in real time via **Kafka UI** at `http://localhost:8085`.

## Stopping

```bash
docker compose down
```

Data in MySQL and MongoDB persists across restarts (stored in Docker
volumes). To wipe it completely:

```bash
docker compose down -v
```

## Possible improvements

- Dead-letter topics and retry handling for failed message processing
- Idempotent consumers to guard against duplicate processing
- Containerize the services themselves, not just the infrastructure
- Kafka Streams for real-time order aggregation
