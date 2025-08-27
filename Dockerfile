# Stage 1: 전달받은 특정 서비스를 빌드
FROM amazoncorretto:21-alpine-jdk AS builder

ARG SERVICE_NAME
WORKDIR /app
COPY . .
WORKDIR /app/${SERVICE_NAME}
RUN chmod +x ./gradlew
RUN ./gradlew bootJar


# Stage 2: 최종 경량 이미지 생성
FROM amazoncorretto:21-alpine-jdk

# healthcheck에 필요한 curl 패키지
RUN apk add --no-cache curl

WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]