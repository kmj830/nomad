# Stage 1: Dependency Caching Layer
FROM eclipse-temurin:17-jdk-jammy AS deps
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Stage 2: Fast Compilation Layer
FROM deps AS build
COPY src src
RUN ./gradlew bootJar --no-daemon -Dorg.gradle.jvmargs="-Xmx384m"

# Stage 3: Lightweight Runtime Layer
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-XX:MaxMetaspaceSize=96m", "-XX:ReservedCodeCacheSize=32m", "-Xss512k", "-XX:+UseSerialGC", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
