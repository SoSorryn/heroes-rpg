# Heroes RPG

Текстовая RPG на Spring Boot.

## Технологии
- Java 21
- Spring Boot 3.3.5
- PostgreSQL
- Thymeleaf
- Lombok

## Как запустить

1. Установите PostgreSQL и создайте базу:
   ```sql
   CREATE DATABASE heroes;
   CREATE USER heroes_user WITH PASSWORD 'heroes_pass';
   GRANT ALL PRIVILEGES ON DATABASE heroes TO heroes_user;
   Как развернуть базу:

## Создайте базу heroes в pgAdmin.
  ```sql
- Кликните правой кнопкой по базе heroes → Restore...
- Выберите файл init.sql (он лежит в корне проекта).
- Нажмите Restore.
- Запустите приложение (mvn spring-boot:run).