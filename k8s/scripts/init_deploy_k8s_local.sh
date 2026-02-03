# 실행 명령어: ./k8s/scripts/init_deploy_k8s_local.sh

set -e

echo "로컬 쿠버네티스 초기 배포 시작"

echo "네임스페이스 생성중..."
kubectl apply -f k8s/namespace/service_namespace.yml

echo "시크릿 생성중..."
./k8s/secret/create_secret.sh

kubectl apply -f k8s/infra/
kubectl apply -f k8s/monitoring/

echo "Redis (Sentinel) 설치중..."
./helm repo add bitnami https://charts.bitnami.com/bitnami
./helm repo update
./helm upgrade --install redis bitnami/redis \
  --set architecture=replication \
  --set sentinel.enabled=true \
  --set auth.existingSecret=omnimfa-secret \
  --set auth.existingSecretPasswordKey=REDIS-PASSWORD \
  --set image.tag=7.2 \
  --wait

echo "서비스 빌드중..."
services=("apigateway-service" "security-service" "onboarding-service" "totp-service")
for service in "${services[@]}"; do
    docker build -t $service:latest --build-arg SERVICE_NAME=$service .
done

echo "서비스 배포중..."
kubectl apply -R -f k8s/service/ -n service

echo "배포 완료"
