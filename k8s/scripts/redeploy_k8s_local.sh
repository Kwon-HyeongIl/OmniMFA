# 실행 명령어: ./k8s/scripts/redeploy_k8s_local.sh

set -e

echo "변경된 서비스만 재배포 시작"

# 전체 서비스 목록
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

echo "배포 대상: ${TARGET_SERVICES[*]}"

for service in "${TARGET_SERVICES[@]}"; do

    echo "[$service] 재배포 진행 중..."
    docker build -t $service:latest --build-arg SERVICE_NAME=$service .
    kubectl rollout restart deployment ${service}-depl
    echo "[$service] 완료"
done

echo "모든 서비스 재배포 요청 완료."