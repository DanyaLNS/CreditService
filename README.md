# Дипломная работа МТС.финтех академии
## Инструкция для запуска проекта 
Сборка проекта реализована с помощью технологии maven. Для запуска небоходимо перейти в корневую директорию проекта и выполнить следующие команды:
1. `mvn clean package`- команда включает в себя компиляцию, проведение UNIT и интеграционных тестов проекта, упаковку исходного кода в распространяемый jar-архив.
2. `java -jar target/CreditService-release.jar` - запуск проекта на локальном хосте, порт 8080.
Модель данных будет создана автоматически с помощью инструмента liquibase.

## Описание эндпоинтов
Проект имеет следующие эндпоинты: 
* **loan_service/getTariffs** - GET - получение списка всех доступных тарифов, не имеет входных параметров и тела запроса;
* **loan_service/order** - POST - подача заявки на кредит, телом запроса является json следующего формата: 
``` json
{
    "userId": 1,
    "tariffId": 1
}
```
* **loan_service/getStatusOrder** - GET - получение статуса заявки с обязательным параметром orderId;
* **loan_service/deleteOrder** - DELETE - удаление заявки на кредит, телом запрос является json следующего формата: 
``` json
{
    "userId": 1,
    "orderId": "qwerty"
}
```
### Функционал был выполнен в соответствии с [техническим заданием](https://drive.google.com/file/d/1zett8xUTBs7ZuF3sFCnykGqPkwLFldb4/view).
Логирование было реализовано с помощью кастомной конфигурации log4j2. Каждый день создаётся новый файл в директории logs, в который записываются логи.

## Frontend 
Также для данного проекта была реализована frontend-часть, при разработке использовался язык программирования JavaScript, фреймворк React и UI-kit Material UI
https://github.com/DanyaLNS/LoanServiceFront
