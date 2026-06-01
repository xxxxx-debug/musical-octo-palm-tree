# Bank application

Spring Boot (Java 11) REST API по [fsd.md](fsd.md). План разработки: [plan.md](plan.md).

## Требования

- JDK 11+ (для локальной сборки)
- Maven 3.8+
- Docker + Docker Compose (полный стек)

## Быстрый старт (Docker Compose)

```bash
docker compose up --build
```

После старта:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/api/health

Остановка: `docker compose down` (данные Postgres в volume `postgres_data`).

### Несколько инстансов (ShedLock + Redis)

```bash
docker compose up --build --scale app=2
```

Порт 8080 будет у одного контейнера; для балансировки добавьте nginx/traefik.

## Локальная разработка без Docker

Каркас без БД:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Полный стек: поднимите Postgres и Redis, затем:

```bash
mvn spring-boot:run
```

## Сборка и тесты

```bash
mvn verify
```

Интеграционные тесты (Testcontainers) требуют Docker.

## Конфигурация

| Переменная | Описание |
|------------|----------|
| `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | PostgreSQL |
| `REDIS_HOST`, `REDIS_PORT` | Redis (кэш, locks, ShedLock) |
| `JWT_SECRET` | Секрет JWT (мин. 32 символа) |
| `SQL_LOG_LEVEL` | `DEBUG` для SQL в dev |

Профиль `docker` активируется в Compose. Профиль `local` отключает Postgres/Redis/Flyway.

## Seed-пользователи

Пароль для всех: `password123`

| ID | Email | Phone | Balance |
|----|-------|-------|---------|
| 1 | ivan@example.com | 79201111111 | 100.00 |
| 2 | maria@example.com | 79202222222 | 500.00 |
| 3 | alex@example.com | 79203333333 | 1000.00 |

## Примеры API

### Login

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"ivan@example.com\",\"password\":\"password123\"}"
```

Сохраните `token` из ответа.

### Профиль

```bash
curl -s http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <token>"
```

### Поиск пользователей

```bash
curl -s "http://localhost:8080/api/users/search?name=Иван&page=1&size=10" \
  -H "Authorization: Bearer <token>"
```

### Перевод

```bash
curl -s -X POST http://localhost:8080/api/transfers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"toUserId\":2,\"amount\":10.50}"
```

Даты: `dd.MM.yyyy`. Пагинация: `page` с 1.

Каждые 30 с баланс растёт на 10% до `max_balance`.
