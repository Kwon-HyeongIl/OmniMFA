# 실행 명령어: ./resources/scripts/redis-standalone-benchmark.sh

set -e

cd "$(dirname "$0")/../.."

COMPOSE_FILE="docker-compose-redis-standalone-benchmark.yml"

echo "벤치마크용 Redis 컨테이너 시작 중..."
docker-compose -f $COMPOSE_FILE up -d

echo "Redis가 준비될 때까지 대기 중 (5초)..."
sleep 5

echo " "
echo "Redis Standalone 부하 테스트"

# 클라이언트 수를 늘려가며 테스트 (100명 -> 1000명 -> 5000명)
for clients in 100 1000 5000; do
    echo " "
    echo "동시 접속자 수: $clients"
    echo "----------------------------------------------------"
    
    docker exec redis-benchmark redis-benchmark -q -n 100000 -c $clients -P 16 -t get
    docker exec redis-benchmark redis-benchmark -q -n 100000 -c $clients -P 16 -t set
done

docker-compose -f $COMPOSE_FILE down
