# 실행 명령어: ./deploy_k8s_local.sh

set -e # 에러 발생 시 스크립트 즉시 종료

echo "로컬 쿠버네티스 배포 시작"

# 1. 인프라/모니터링 배포
echo "인프라/모니터링 배포 시작"
kubectl apply -f k8s/infra/
kubectl apply -f k8s/monitoring/

# 2. Redis Sentinel 설치
echo "Redis (Sentinel) 설치 중..."
./helm repo add bitnami https://charts.bitnami.com/bitnami
./helm repo update
./helm upgrade --install redis bitnami/redis \
  --set architecture=replication \
  --set sentinel.enabled=true \
  --set auth.existingSecret=omnimfa-secret \
  --set auth.existingSecretPasswordKey=REDIS-PASSWORD \
  --set image.tag=7.2 \
  --wait

# 2. 서비스 빌드
echo "서비스 빌드 시작"
services=("apigateway-service" "security-service" "onboarding-service" "totp-service")
for service in "${services[@]}"; do
    docker build -t $service:latest --build-arg SERVICE_NAME=$service .
done

# 3. 서비스 배포
echo "서비스 배포 시작"
kubectl apply -f k8s/service/

echo "배포 완료"
