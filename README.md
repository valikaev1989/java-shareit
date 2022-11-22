---

# *Java-shareit*

Проект по курсу JAVA-developer от [Яндекс-Практикума](https://practicum.yandex.ru/java-developer/).

Описание проекта
-
Приложение предоставляющее возможность сдавать в аренду свои предметы и арендовать предметы у других пользователей.

Использованные технологии:
-

- Java Coretto 11,Maven, Spring-Boot, Hibernate, Postgresql, Lombok, Docker-compose 3.8, Jpa,H2Database, RestTemplate

Функционал приложения:
-

1. ### Проект реализован по микро-сервисной архитектуре:
    * gateway - валидация входящих в запрос данных
    * server - обработка запроса и возвращение ответа

2. ### Основной функционал:

    * Создавать\редактировать\получать\удалять пользователя
    * Создавать\редактировать\получать\удалять предмет пользователем
    * создавать\удалять комментарии
    * Создавать\редактировать статус бронирования владельцем предмета\получать информацию о бронировании предмета
    * создавать\получать информацию о запросах на бронирование предмета

3. ### Схема базы данных приложения:

[](https://github.com/valikaev1989/java-shareit/blob/main/server/src/main/resources/database.PNG)

Инструкция по запуску:
-

1. Чтобы запустить сервисы по отдельности (через main) нужна запущенная бд Postgres. С помощью pgAdmin4 создайте базу
   данных:
   Необходимо создать базу данных postgreSQL _**shareit**_:
   * POSTGRES_USER = shareituser
   * POSTGRES_PASSWORD = shareit
   * POSTGRES_DB = shareit

2. Для запуска проекта потребуется docker и docker-compose.
3. Команда "docker-compose up" запускает оба сервиса с бд
4. Для проверки работоспособности приложения предусмотрены тесты для приложения постман:
[shareit-tests](https://github.com/valikaev1989/java-shareit/blob/main/postmanTests/tests.json)
Также в этом проекте было реализовано внутренние тесты с покрытием кода на 99%
[qwe](https://github.com/valikaev1989/java-shareit/blob/main/media/coverage.PNG)
