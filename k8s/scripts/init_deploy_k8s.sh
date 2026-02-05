# 실행 명령어: ./k8s/scripts/init_deploy_k8s.sh

set -e

echo "로컬 쿠버네티스 초기 배포 시작"

# 1. Docker Hub 로그인
./k8s/scripts/docker_login.sh

# 2. 네임스페이스 생성
echo "네임스페이스 생성 중..."
kubectl apply -f k8s/namespace/service_namespace.yml
kubectl apply -f k8s/namespace/infra_namespace.yml

# 3. Ingress Controller 설치
echo "NGINX Ingress Controller 설치 중..."
./helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
./helm repo update

./helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace infra \
  --wait

# 4. 시크릿 생성
echo "시크릿 생성 중..."
./k8s/secret/create_secret.sh

# 5. 인프라 자원 배포
echo "인프라 자원 배포 중..."
kubectl apply -f k8s/infra/db/mysql.yml
kubectl apply -f k8s/infra/monitoring/rbac.yml
kubectl apply -f k8s/infra/monitoring/prometheus.yml
kubectl apply -f k8s/infra/monitoring/grafana.yml
kubectl apply -f k8s/infra/monitoring/metrics-server.yml

# 6. Redis (Sentinel) 설치
echo "Redis (Sentinel) 설치 중..."
./helm repo add bitnami https://charts.bitnami.com/bitnami
./helm repo update
./helm upgrade --install redis bitnami/redis -n infra \
  --set architecture=replication \
  --set sentinel.enabled=true \
  --set auth.existingSecret=omnimfa-secret \
  --set auth.existingSecretPasswordKey=REDIS-PASSWORD \
  --wait

# 7. 서비스 빌드
echo "서비스 빌드 중..."
services=("apigateway-service" "security-service" "onboarding-service" "totp-service")
for service in "${services[@]}"; do
    (
        IMAGE_NAME="kwonhyeongil/$service:latest"
        docker build -t $IMAGE_NAME --build-arg SERVICE_NAME=$service .
        docker push $IMAGE_NAME
    ) &
done

wait

# 8. 서비스 배포
echo "서비스 배포 중..."
kubectl apply -f k8s/service/depl/

# 9. Ingress 적용
echo "Ingress 리소스 배포 중..."
kubectl apply -f k8s/infra/network/ingress.yml

# 10. Grafana 포트 포워딩 (Background)
echo "Grafana 포트 포워딩 설정 중..."
lsof -ti:3000 | xargs kill -9 2>/dev/null || true
nohup kubectl port-forward -n infra svc/grafana-k8ssvc 3000:3000 > /dev/null 2>&1 &

echo "배포 완료"