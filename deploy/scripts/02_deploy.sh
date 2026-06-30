#!/usr/bin/env bash
# JAR 배포 + systemd 재시작.
# 실행 위치: EC2 안에서.
# 사용법: 02_deploy.sh [jar-path]
#   jar-path 기본값: /tmp/app.jar
#
# 로컬에서 빌드 후 scp 로 /tmp/app.jar 에 올려둔 다음 이 스크립트를 실행.
#   (로컬) ./gradlew :app:bootJar
#   (로컬) scp app/build/libs/app-0.0.1-SNAPSHOT.jar <EC2>:/tmp/app.jar
#   (EC2) ./02_deploy.sh

set -euo pipefail

JAR_PATH="${1:-/tmp/app.jar}"

if [[ ! -f "$JAR_PATH" ]]; then
    echo "❌ jar 파일을 찾을 수 없습니다: $JAR_PATH" >&2
    echo "   사용법: $0 [jar-path]" >&2
    exit 1
fi

echo "[1/3] 기존 jar 백업..."
if [[ -f /opt/moball/app.jar ]]; then
    sudo cp /opt/moball/app.jar "/opt/moball/app.jar.bak-$(date +%Y%m%d-%H%M%S)"
fi

echo "[2/3] 새 jar 배치..."
sudo mv "$JAR_PATH" /opt/moball/app.jar
sudo chown moball:moball /opt/moball/app.jar

echo "[3/3] 서비스 재시작..."
sudo systemctl restart moball-backend
sleep 2
sudo systemctl status moball-backend --no-pager --lines=20

echo ""
echo "✅ 배포 완료. 실시간 로그:"
echo "  sudo journalctl -u moball-backend -f"
