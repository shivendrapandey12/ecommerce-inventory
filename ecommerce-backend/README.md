# E-Commerce Order and Inventory Processing System

A Spring Boot REST backend for products, inventory, orders and order items, built to stay **consistent under concurrent load**. It reproduces the classic *inventory overselling* bug (many customers buying the last items at the same time) and fixes it with database transactions and JPA optimistic locking.

## The problem

When several purchase requests hit a product with limited stock at the same time, each request reads the same stock value, each decides "there is enough", and each subtracts from it. The result is negative or wrong stock: items are sold that do not exist.

## The solution

- **Transactions** (`@Transactional`) so stock deduction and order creation succeed or roll back together.
- **Optimistic locking** (`@Version` on the inventory/product entity). If two requests update the same row, the second one fails with a version conflict instead of silently overwriting.
- **Centralized exception handling** (`@RestControllerAdvice`) that turns "insufficient stock" and "version conflict" into clear HTTP error responses.
- **Concurrency test** using `ExecutorService` that fires many simultaneous purchases at limited stock and asserts the final stock is never negative.

## Tech stack

Java 21, Spring Boot, Spring Data JPA, Hibernate, MySQL, JUnit, Docker, Maven

## Architecture

Layered design: **Controller -> Service -> Repository -> Entity**, with DTOs between the controller and service layers.

```
src/main/java/com/shivendra/ecommerce_backend
 |- controller/   REST endpoints
 |- service/      business logic and transactions
 |- repository/   Spring Data JPA repositories
 |- entity/       Product, Inventory, Order, OrderItem (with @Version)
 |- dto/          request / response objects
 |- exception/    custom exceptions + global handler
```

## Prerequisites

| Tool | Version | Check with |
|------|---------|-----------|
| JDK | 21 | `java -version` |
| Maven | 3.9+ (or use the included `./mvnw`) | `mvn -v` |
| MySQL | 8+ | `mysql --version` |
| Docker (optional) | any recent | `docker -v` |

## How to build and run (step by step)

### 1. Clone the repository
```bash
git clone https://github.com/shivendrapandey12/ecommerce-inventory.git
cd ecommerce-inventory
```

### 2. Create the database
```sql
CREATE DATABASE ecommerce_db;
```

### 3. Configure the connection
Edit `src/main/resources/application.properties`:
```properties
spring.application.name=ecommerce-backend
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
Do not commit real passwords. Use environment variables or a local-only properties file.

### 4. Build
```bash
./mvnw clean install        # Windows: mvnw.cmd clean install
```

### 5. Run
```bash
./mvnw spring-boot:run
```
The API starts on `http://localhost:8080`.

### 6. Run the tests
```bash
./mvnw test
```
This includes the concurrency test that checks stock stays consistent.

### 7. Run with Docker (optional)
```bash
docker build -t ecommerce-inventory .
docker run -p 8080:8080 ecommerce-inventory
```
Or use Docker Compose to start the app and MySQL together:
```bash
docker compose up --build
```

## API endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create a product with stock |
| GET | `/api/products` | List products |
| GET | `/api/products/{id}` | Get one product |
| POST | `/api/orders` | Place an order (deducts stock) |
| GET | `/api/orders/{id}` | Get an order with its items |

Example request:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"items":[{"productId":1,"quantity":2}]}'
```

Error responses:
- `409 Conflict` - version conflict (another request changed the stock first)
- `400 Bad Request` - insufficient stock

## Author

Shivendra Pandey - [GitHub](https://github.com/shivendrapandey12) | [LinkedIn](https://www.linkedin.com/in/shivendra-pandey-8377a02a3/)
