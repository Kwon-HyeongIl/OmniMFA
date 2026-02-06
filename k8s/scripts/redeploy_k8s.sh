# 실행 명령어: ./k8s/scripts/redeploy_k8s.sh

set -e


# 1. Docker Hub 로그인
./k8s/scripts/docker_login.sh

# 2. 변경된 서비스 탐색
echo "변경된 서비스만 재배포 시작"
ALL_SERVICES=("apigateway-service" "security-service" "onboarding-service" "totp-service")
TARGET_SERVICES=()
echo "변경된 서비스 검색 중..."
for service in "${ALL_SERVICES[@]}"; do
    if [ -n "$(git status --porcelain "$service")" ]; then
        TARGET_SERVICES+=("$service")
    fi
done

# 변경사항이 없으면 종료
if [ ${#TARGET_SERVICES[@]} -eq 0 ]; then
    echo "⚠️ 변경된 서비스가 없습니다."
    exit 0
fi

# 3. 변경된 서비스 빌드 및 배포
echo "배포 대상: ${TARGET_SERVICES[*]}"
for service in "${TARGET_SERVICES[@]}"; do
    (
        echo "[$service] 재배포 진행 중..."
        IMAGE_NAME="kwonhyeongil/$service:latest"
        docker build -t $IMAGE_NAME --build-arg SERVICE_NAME=$service .
        docker push $IMAGE_NAME
        kubectl rollout restart deployment ${service}-depl -n service
        echo "[$service] 완료"
    ) &
done

wait

echo "모든 서비스 재배포 요청 완료."