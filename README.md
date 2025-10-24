![Image](https://github.com/user-attachments/assets/b70602af-f9a5-4976-94d0-9123d4680627)

<br><br>

## 소개
OmniMFA는 Spring 기반 MSA 아키텍처로 구축된 멀티팩터 인증(MFA) 서비스로, 외부 서비스가 손쉽게 2단계 인증을 사용할 수 있도록 표준화된 API를 제공합니다. 사용자는 Google Authenticator로 QR 세팅 후 일회용 코드를 통해 안전하게 로그인할 수 있습니다. 

OmniMFA를 이용하는 서비스의 개발팀은 짧은 시간에 연동을 완료하고, 운영팀은 안정적인 서비스 운영과 확장성을 누릴 수 있습니다. 현재는 TOTP 방식의 MFA 인증 방식을 제공하며, 향후 비즈니스 요구에 맞춰 추가 인증 수단을 유연하게 확장할 계획입니다.

<br><br>

## 사용된 기술
### 애플리케이션
- 언어: Java
- 프레임워크: Spring, Spring Security, Spring WebFlux
- 아키텍처: MSA
- 게이트웨이: Spring Cloud Gateway
- 문서화: Swagger UI

### 인증 & 보안
- MFA 방식: TOTP
- 토큰: JWT

### 데이터 & 메시징
- 데이터베이스: MySQL (Amazon RDS)
- ORM: Sprint Data JPA
- 캐시: Redis (EKS 클러스터 내 Deployment)
- 메시지 브로커: Kafka (EKS 클러스터 내 Deployment, Zookeeper 포함)

### 인프라 & 배포
- 플랫폼: Amazon EKS (Kubernetes)
- 컨테이너: Docker
- 레지스트리: Amazon ECR
- DNS: Amazon Route53
- 헬스 체크: Spring Acuator
- CI/CD: Github Actions

<br><br>

## 기술적인 내용


고민했던 부분

개선점



