# 💳 Mini Bank Application
This is a simple banking system built with **Spring Boot** and **PostgreSQL**.

It supports:
- Creating and managing customers
- Creating and managing accounts
- Deposits and withdrawals
- Transferring money
- Viewing transaction history and account balances

## 📦 Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Swagger (OpenAPI 3)


## 🚀 How to Run

1. Make sure PostgreSQL is running.
2. Create a database named `minibank`.
3. Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minibank
spring.datasource.username=your_username
spring.datasource.password=your_password

📂 API Documentation (Swagger)
After the application starts, go to:

➡️ http://localhost:8081/swagger-ui/index.html

You can test the APIs directly from the browser.

