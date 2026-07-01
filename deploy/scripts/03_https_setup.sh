#!/usr/bin/env bash
# Nginx + Let's Encrypt HTTPS 셋업.
# 실행 위치: EC2 안에서, 01_bootstrap.sh 이후 1회.
# 사용법: 03_https_setup.sh <domain> <email>
#   예: 03_https_setup.sh moball.duckdns.org kouig14@gmail.com
#
# 사전 조건:
#   - DuckDNS A 레코드가 이 EC2 의 퍼블릭 IP 를 가리키고 있어야 함
#   - EC2 보안그룹 80/443 열려 있어야 함

set -euo pipefail

if [[ $# -lt 2 ]]; then
    echo "❌ 사용법: $0 <domain> <email>" >&2
    echo "   예: $0 moball.duckdns.org kouig14@gmail.com" >&2
    exit 1
fi

DOMAIN="$1"
EMAIL="$2"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
CONF_SRC="$DEPLOY_DIR/nginx-moball.conf"
CONF_DST="/etc/nginx/sites-available/moball.conf"

echo "[1/4] Nginx conf 배치 (도메인: $DOMAIN)..."
sudo cp "$CONF_SRC" "$CONF_DST"
sudo sed -i "s|<YOUR_DOMAIN>|$DOMAIN|g" "$CONF_DST"

if [[ ! -L /etc/nginx/sites-enabled/moball.conf ]]; then
    sudo ln -s "$CONF_DST" /etc/nginx/sites-enabled/moball.conf
fi

# 기본 default site 비활성 (포트 80 충돌 방지)
if [[ -L /etc/nginx/sites-enabled/default ]]; then
    sudo rm /etc/nginx/sites-enabled/default
fi

echo "[2/4] Nginx 문법 검증 + 리로드..."
sudo nginx -t
sudo systemctl reload nginx

echo "[3/4] certbot 설치 + HTTPS 인증서 발급..."
sudo apt-get install -y certbot python3-certbot-nginx
sudo certbot --nginx \
    -d "$DOMAIN" \
    --agree-tos \
    --no-eff-email \
    -m "$EMAIL" \
    --redirect \
    --non-interactive

echo "[4/4] 자동 갱신 타이머 확인..."
sudo systemctl status certbot.timer --no-pager --lines=5

echo ""
echo "✅ HTTPS 셋업 완료."
echo "   브라우저로 확인: https://$DOMAIN/swagger-ui.html"
echo "   인증서 갱신 테스트: sudo certbot renew --dry-run"
