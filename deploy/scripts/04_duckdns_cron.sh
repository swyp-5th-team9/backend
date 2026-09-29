#!/usr/bin/env bash
# DuckDNS IP 자동 갱신 cron 등록.
# 실행 위치: EC2 안에서, 1회.
# 사용법: 04_duckdns_cron.sh <subdomain> <token>
#   예: 04_duckdns_cron.sh moball 72bfb648-aac8-447a-b50b-d80aa09dcb69
#
# EC2 재시작 시 퍼블릭 IP 가 바뀔 수 있어 5분마다 DuckDNS 에 현재 IP 를 푸시.

set -euo pipefail

if [[ $# -lt 2 ]]; then
    echo "❌ 사용법: $0 <subdomain> <token>" >&2
    echo "   예: $0 moball <DUCKDNS_TOKEN>" >&2
    exit 1
fi

SUBDOMAIN="$1"
TOKEN="$2"

DUCK_DIR="$HOME/duckdns"
DUCK_SH="$DUCK_DIR/duck.sh"

echo "[1/3] ~/duckdns 디렉토리 생성..."
mkdir -p "$DUCK_DIR"

echo "[2/3] 갱신 스크립트 작성..."
cat > "$DUCK_SH" <<EOF
#!/usr/bin/env bash
echo url="https://www.duckdns.org/update?domains=$SUBDOMAIN&token=$TOKEN&ip=" | curl -k -o $DUCK_DIR/duck.log -K -
EOF
chmod 700 "$DUCK_SH"

echo "[3/3] crontab 등록 (5분마다)..."
# 기존 duckdns 라인 제거 후 신규 추가 (멱등성)
CRON_LINE="*/5 * * * * $DUCK_SH >/dev/null 2>&1"
( crontab -l 2>/dev/null | grep -v "duckdns/duck.sh" ; echo "$CRON_LINE" ) | crontab -

echo ""
echo "✅ DuckDNS cron 등록 완료."
echo ""
echo "즉시 1회 갱신 테스트:"
echo "  bash $DUCK_SH && cat $DUCK_DIR/duck.log"
echo "  → 응답이 'OK' 면 성공"
echo ""
echo "현재 cron 확인:"
echo "  crontab -l"
