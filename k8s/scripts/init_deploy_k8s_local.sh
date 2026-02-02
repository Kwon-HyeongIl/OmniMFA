# 실행 명령어: ./k8s/scripts/init_deploy_k8s_local.sh

set -e

echo "로컬 쿠버네티스 초기 배포 시작"

# 1. 인프라/모니터링 배포
echo "인프라/모니터링 배포 시작"
kubectl apply -f k8s/infra/
kubectl apply -f k8s/monitoring/

echo "Redis (Sentinel) 설치 시작"
./helm repo add bitnami https://charts.bitnami.com/bitnami
./helm repo update
./helm upgrade --install redis bitnami/redis \
  --set architecture=replication \
  --set sentinel.enabled=true \
  --set auth.existingSecret=omnimfa-secret \
  --set auth.existingSecretPasswordKey=REDIS-PASSWORD \
  --set image.tag=7.2 \
  --wait

# 3. 서비스 빌드
echo "서비스 빌드 시작"
services=("apigateway-service" "security-service" "onboarding-service" "totp-service")
for service in "${services[@]}"; do
    docker build -t $service:latest --build-arg SERVICE_NAME=$service .
done

# 4. 서비스 배포
echo "서비스 배포 시작"
kubectl apply -R -f k8s/service/

echo "배포 완료"
