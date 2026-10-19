# Используем легковесный образ Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный jar-файл внутрь контейнера
COPY target/*.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]