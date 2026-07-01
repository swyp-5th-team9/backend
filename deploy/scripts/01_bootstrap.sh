#!/usr/bin/env bash
# EC2 (Ubuntu 22.04) 초기 셋업.
# 실행 위치: EC2 안에서, repo clone 후 backend/deploy/scripts/ 에서.
# 멱등성 보장 — 여러 번 실행해도 안전.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "[1/4] apt 패키지 설치 (Java 21, Nginx)..."
sudo apt-get update
sudo apt-get install -y openjdk-21-jre-headless nginx

echo "[2/4] moball 시스템 유저 생성..."
if ! id moball >/dev/null 2>&1; then
    sudo useradd -r -s /usr/sbin/nologin moball
    echo "  → moball 유저 생성 완료"
else
    echo "  → moball 유저 이미 존재 (skip)"
fi

echo "[3/4] 디렉토리 생성 + 권한 설정..."
sudo mkdir -p /opt/moball /etc/moball /var/log/moball
sudo chown -R moball:moball /opt/moball /var/log/moball
sudo chmod 750 /etc/moball

echo "[4/4] systemd 유닛 등록..."
sudo cp "$DEPLOY_DIR/moball-backend.service" /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable moball-backend

echo ""
echo "✅ bootstrap 완료."
echo ""
echo "다음 단계:"
echo "  1) .env.example 참고하여 /etc/moball/moball.env 생성 + 값 입력"
echo "     sudo cp $DEPLOY_DIR/.env.example /etc/moball/moball.env"
echo "     sudo nano /etc/moball/moball.env"
echo "     sudo chown root:moball /etc/moball/moball.env"
echo "     sudo chmod 640 /etc/moball/moball.env"
echo "  2) 02_deploy.sh 로 jar 배포"
