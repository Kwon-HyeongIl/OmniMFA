# Stage 1: 전달받은 특정 서비스를 빌드
# Alpine 대신 glibc가 포함된 일반 Linux 이미지를 사용하여 gRPC 빌드 호환성 문제 해결
FROM amazoncorretto:21 AS builder

ARG SERVICE_NAME

WORKDIR /app
COPY . .
WORKDIR /app/${SERVICE_NAME}
RUN chmod +x ./gradlew
RUN ./gradlew bootJar


# Stage 2: 최종 경량 이미지 생성
FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache curl

WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]