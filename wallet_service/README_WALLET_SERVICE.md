### Wallet Service (wallet-service/)
   ```text
   src/main/java/com/yourcompany/walletservice/
   ├── config/
   │   ├── DatabaseConfig.java          # Настройка DataSource, JPA
   │   ├── GrpcServerConfig.java        # Конфигурация gRPC сервера
   │   ├── SecurityConfig.java          # (Бонус) Конфиг Spring Security + JWT
   │   └── WebConfig.java               # Конфиг для REST (если нужно)
   ├── controller/
   │   └── (может быть пустым, если только gRPC)
   ├── service/
   │   ├── AccountService.java          # Основная логика работы со счетами
   │   ├── TransactionService.java      # Логика проведения транзакций
   │   └── UserService.java             # Логика работы с пользователями
   ├── repository/
   │   ├── AccountRepository.java       # JPA Repository для accounts
   │   ├── TransactionRepository.java   # JPA Repository для transactions
   │   └── UserRepository.java          # JPA Repository для users
   ├── model/
   │   ├── entity/
   │   │   ├── UserEntity.java
   │   │   ├── AccountEntity.java
   │   │   └── TransactionEntity.java
   │   ├── dto/
   │   │   ├── BalanceResponse.java
   │   │   ├── TransferRequest.java
   │   │   └── ApiError.java            # DTO для ошибок REST API
   │   └── record/                      # Альтернатива DTO
   │       └── BalanceRecord.java
   ├── grpc/
   │   ├── service/
   │   │   └── WalletGrpcService.java   # implements Grpc.WalletServiceGrpc.WalletServiceImplBase
   │   └── model/                       # Сгенерированные классы из proto
   │       └── wallet_grpc.java
   │       └── wallet_proto.java
   ├── kafka/
   │   └── consumer/                    # Wallet слушает trades-topic
   │       └── TradeEventConsumer.java  # Чтобы обновлять балансы после сделок
   └── exception/
   ├── InsufficientFundsException.java
   ├── AccountNotFoundException.java
   └── GlobalExceptionHandler.java  # @ControllerAdvice для маппинга исключений
```

### **Архитектура базы данных Wallet Service**

Мы создадим несколько таблиц, которые обеспечат надежное хранение данных, транзакционность и аудит всех операций.

---

### **1. Таблица `users` (Пользователи)**

Хранит основную информацию о пользователях. Пароли не хранятся в открытом виде.

| Поле             | Тип данных         | Ограничения                  | Описание                                                                 |
|-------------------|--------------------|------------------------------|-------------------------------------------------------------------------|
| `id`              | `BIGSERIAL`        | `PRIMARY KEY`                | Уникальный идентификатор пользователя (UUID лучше, но BIGSERIAL проще) |
| `username`        | `VARCHAR(50)`      | `UNIQUE NOT NULL`            | Логин пользователя (для входа в систему)                                |
| `email`           | `VARCHAR(255)`     | `UNIQUE NOT NULL`            | Email пользователя (для уведомлений и восстановления)                  |
| `password_hash`   | `VARCHAR(255)`     | `NOT NULL`                   | **Хэш** пароля (никогда не храните пароли в открытом виде!)            |
| `status`          | `VARCHAR(20)`      | `DEFAULT 'ACTIVE'`           | Статус: `ACTIVE`, `BLOCKED`, `DELETED` (soft delete)                   |
| `created_at`      | `TIMESTAMPTZ`      | `DEFAULT NOW()`              | Дата и время регистрации                                               |
| `updated_at`      | `TIMESTAMPTZ`      | `DEFAULT NOW()`              | Дата и время последнего обновления                                     |

**Важно о безопасности:**
*   Используйте современный алгоритм хэширования, например, **bcrypt** (реализация `BCryptPasswordEncoder` в Spring Security). Он включает "соль" (salt) и является computationally expensive, что защищает от подбора.
*   Поле `status` полезно для блокировки пользователя без удаления его данных (soft delete).

---

### **2. Таблица `accounts` (Счета)**

У одного пользователя может быть несколько счетов в разных валютах (USD, BTC, ETH, etc.). Это основная таблица для учета балансов.

| Поле             | Тип данных         | Ограничения                          | Описание                                                              |
|-------------------|--------------------|--------------------------------------|----------------------------------------------------------------------|
| `id`              | `BIGSERIAL`        | `PRIMARY KEY`                        | Уникальный идентификатор счета                                       |
| `user_id`         | `BIGINT`           | `NOT NULL REFERENCES users(id)`      | Внешний ключ на пользователя                                         |
| `currency`        | `VARCHAR(10)`      | `NOT NULL`                           | Код валюты: `USD`, `BTC`, `ETH` (лучше использовать стандартные коды)|
| `available_balance` | `NUMERIC(30, 15)`  | `DEFAULT 0.0 CHECK >= 0`             | **Свободный баланс** - средства, доступные для использования в ордерах |
| `reserved_balance`  | `NUMERIC(30, 15)`  | `DEFAULT 0.0 CHECK >= 0`             | **Зарезервированный баланс** - средства, заблокированные в активных ордерах |
| `total_balance`   | `GENERATED ALWAYS AS (available_balance + reserved_balance) STORED` | | **Вычисляемый итоговый баланс** (для удобства и отчетности) |
| `created_at`      | `TIMESTAMPTZ`      | `DEFAULT NOW()`                      | Дата создания счета                                                  |
| `updated_at`      | `TIMESTAMPTZ`      | `DEFAULT NOW()`                      | Дата последнего изменения баланса                                    |

**Ключевой момент:**
*   **`total_balance = available_balance + reserved_balance`**. Это правило должно соблюдаться всегда. Любая финансовая операция — это перемещение средств между этими двумя колонками или между счетами разных пользователей.
*   `NUMERIC(30, 15)` выбран для хранения криптовалют, где может быть важно много знаков после запятой. Для традиционных валют можно использовать `NUMERIC(15, 2)`.
*   **Индексы:** Обязательно создайте составной индекс `(user_id, currency)` для быстрого поиска счетов пользователя и уникальный индекс `(user_id, currency)` чтобы у пользователя был только один счет в конкретной валюте.

---

### **3. Таблица `transactions` (Транзакции/Операции)**

**Самая важная таблица для аудита.** В нее записывается каждая операция со средствами. Это журнал всех событий, который нельзя изменить (append-only).

| Поле                 | Тип данных         | Ограничения                               | Описание                                                                 |
|-----------------------|--------------------|-------------------------------------------|-------------------------------------------------------------------------|
| `id`                  | `BIGSERIAL`        | `PRIMARY KEY`                            | Уникальный идентификатор транзакции (можно использовать UUID)          |
| `debit_account_id`    | `BIGINT`           | `REFERENCES accounts(id)`                | **Счет списания** (откуда ушли деньги). Может быть `NULL` (пополнение).|
| `credit_account_id`   | `BIGINT`           | `REFERENCES accounts(id)`                | **Счет зачисления** (куда пришли деньги). Может быть `NULL` (вывод).   |
| `type`                | `VARCHAR(50)`      | `NOT NULL`                               | Тип операции: `ORDER_RESERVE`, `ORDER_RELEASE`, `TRADE`, `DEPOSIT`, `WITHDRAWAL`, `FEE` |
| `amount`              | `NUMERIC(30, 15)`  | `NOT NULL CHECK (amount > 0)`            | Сумма операции                                                         |
| `currency`            | `VARCHAR(10)`      | `NOT NULL`                               | Валюта операции (дублирование для удобства запросов)                   |
| `status`              | `VARCHAR(20)`      | `DEFAULT 'PENDING'`                      | Статус: `PENDING`, `COMPLETED`, `FAILED`, `ROLLED_BACK`                |
| `related_object_id`   | `VARCHAR(100)`     |                                           | ID связанного объекта (например, ID ордера или сделки из Kafka) для трассировки |
| `related_object_type` | `VARCHAR(50)`      |                                           | Тип связанного объекта: `ORDER`, `TRADE`                               |
| `description`         | `TEXT`             |                                           | Произвольное описание операции (для дебага и логов)                    |
| `created_at`          | `TIMESTAMPTZ`      | `DEFAULT NOW()`                          | Дата и время создания транзакции                                       |
| `updated_at`          | `TIMESTAMPTZ`      | `DEFAULT NOW()`                          | Дата и время обновления статуса                                        |

**Принцип двойной записи (Double-Entry Accounting):**
Каждая финансовая операция затрагивает как минимум два счета (дебет и кредит). Например, резервирование средств на ордер:
*   **Дебетуем** `reserved_balance` (увеличиваем)
*   **Кредитуем** `available_balance` (уменьшаем) на том же самом счете.
    Это гарантирует, что общий баланс (`total_balance`) всегда остается неизменным до момента совершения сделки.

---

### **Схема взаимосвязей (SQL DDL)**

```sql

Table users {
  id uuid [primary key]
  username varchar
  email varchar
  password_hash varchar
  status varchar
  created_at timestamp
  updated_at timestamp
}

Table accounts {
  id uuid [primary key]             
  user_id uuid
  currency varchar          
  available_balance integer 
  reserved_balance integer  
  total_balance integer     
  created_at timestamp       
  updated_at timestamp
}

Table transactions {
  id uuid [primary key]                
  debit_account_id uuid    
  credit_account_id uuid   
  type varchar               
  amount integer             
  currency varchar             
  status varchar               
  related_object_id varchar    
  related_object_type varchar  
  description varchar          
  created_at timestamp           
  updated_at timestamp
}

Ref: users.id < accounts.user_id

Ref: accounts.id < transactions.credit_account_id

Ref: accounts.id < transactions.debit_account_id

```

---

### **Логика работы Wallet Service**

1.  **Order Service отправляет запрос:** "Зарезервировать 100 USD для ордера #123 пользователя #456".
2.  **Wallet Service (в транзакции):**
    *   Проверяет, что у пользователя `available_balance >= 100 USD`.
    *   Уменьшает `available_balance` на 100.
    *   Увеличивает `reserved_balance` на 100.
    *   **Создает две записи в `transactions`** (дебет и кредит внутри одного счета):
        *   `(debit_account_id=acc_id, credit_account_id=NULL, type='ORDER_RESERVE', amount=100, ...)`
        *   `(debit_account_id=NULL, credit_account_id=acc_id, type='ORDER_RESERVE', amount=100, ...)` (или одна запись с двумя account_id).
    *   Фиксирует транзакцию.
3.  **При отмене ордера** — операция в обратную сторону.
4.  **При исполнении ордера (сделке) из `trades-topic`:**
    *   Wallet Service consumes сообщение о сделке.
    *   В транзакции списывает зарезервированные средства с покупателя и продавца и зачисляет их на соответствующие счета (например, продавец получает USD, покупатель получает BTC).
    *   Создает соответствующие записи в `transactions`.

Это надежная и аудируемая архитектура, которая является стандартом для финансовых систем.


























