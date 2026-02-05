# 실행 명령어: ./k8s/scripts/init_deploy_k8s.sh

set -e

echo "로컬 쿠버네티스 초기 배포 시작"

# 1. Docker Hub 로그인
./k8s/scripts/docker_login.sh

# 2. 네임스페이스 생성
echo "네임스페이스 생성중..."
kubectl apply -f k8s/namespace/service_namespace.yml
kubectl apply -f k8s/namespace/infra_namespace.yml

# 3. 시크릿 생성
echo "시크릿 생성중..."
./k8s/secret/create_secret.sh

# 4. 인프라 자원 배포
echo "인프라 자원 배포중..."
kubectl apply -f k8s/infra/db/mysql.yml
kubectl apply -f k8s/infra/monitoring/rbac.yml
kubectl apply -f k8s/infra/monitoring/prometheus.yml
kubectl apply -f k8s/infra/monitoring/grafana.yml
kubectl apply -f k8s/infra/monitoring/metrics-server.yml

# 5. Redis (Sentinel) 설치
echo "Redis (Sentinel) 설치중..."
./helm repo add bitnami https://charts.bitnami.com/bitnami
./helm repo update
./helm upgrade --install redis bitnami/redis -n infra \
  --set architecture=replication \
  --set sentinel.enabled=true \
  --set auth.existingSecret=omnimfa-secret \
  --set auth.existingSecretPasswordKey=REDIS-PASSWORD \
  --wait

# 6. 서비스 빌드
echo "서비스 빌드중..."
services=("apigateway-service" "security-service" "onboarding-service" "totp-service")
for service in "${services[@]}"; do
    (
        IMAGE_NAME="kwonhyeongil/$service:latest"
        docker build -t $IMAGE_NAME --build-arg SERVICE_NAME=$service .
        docker push $IMAGE_NAME
    ) &
done

wait

# 7. 서비스 배포
echo "서비스 배포중..."
kubectl apply -R -f k8s/service/ -n service

echo "배포 완료"
