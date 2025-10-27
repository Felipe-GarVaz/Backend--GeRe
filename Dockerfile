# Etapa 1: Build del frontend con Node
FROM node:20 AS frontend-builder
WORKDIR /app/frontend
COPY ./frontend/package*.json ./
RUN npm install
COPY ./frontend .
RUN npm run build

# Etapa 2: Build del backend con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS backend-builder
WORKDIR /app
COPY ./backend/pom.xml .
COPY ./backend/src ./src
RUN mvn clean package -DskipTests

# Etapa 3: Imagen final (backend + frontend)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el backend compilado
COPY --from=backend-builder /app/target/*.jar app.jar

# Copiamos el build del frontend (React) dentro de static/
COPY --from=frontend-builder /app/frontend/build /app/static

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]