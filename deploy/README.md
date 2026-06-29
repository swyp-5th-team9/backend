# Sportspub Backend 운영 배포 가이드

EC2 (Ubuntu 22.04) + RDS (PostgreSQL 16) + Nginx + DuckDNS + Let's Encrypt 기준.

## 사전 준비
- AWS 프리티어 계정
- DuckDNS 서브도메인 + 토큰 (`https://www.duckdns.org`)
- 카카오/네이버 OAuth 키 (선택)

## 1. EC2 초기 셋업

```bash
# Java 21
sudo apt update
sudo apt install -y openjdk-21-jre-headless nginx

# 앱 유저/디렉토리
sudo useradd -r -s /usr/sbin/nologin sportspub
sudo mkdir -p /opt/sportspub /etc/sportspub /var/log/sportspub
sudo chown -R sportspub:sportspub /opt/sportspub /var/log/sportspub
sudo chmod 750 /etc/sportspub
```

## 2. 환경 변수 파일

```bash
sudo cp deploy/.env.example /etc/sportspub/sportspub.env
sudo nano /etc/sportspub/sportspub.env   # 실제 값 입력
sudo chown root:sportspub /etc/sportspub/sportspub.env
sudo chmod 640 /etc/sportspub/sportspub.env
```

## 3. systemd 등록

```bash
sudo cp deploy/sportspub-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable sportspub-backend
```

## 4. JAR 배포 (수동)

```bash
# 로컬에서 빌드
./gradlew :app:bootJar
scp app/build/libs/app-0.0.1-SNAPSHOT.jar ec2:/tmp/app.jar

# EC2 에서
sudo mv /tmp/app.jar /opt/sportspub/app.jar
sudo chown sportspub:sportspub /opt/sportspub/app.jar
sudo systemctl restart sportspub-backend
sudo journalctl -u sportspub-backend -f
```

## 5. Nginx + HTTPS

```bash
# DuckDNS 도메인 박은 conf 배치
sudo cp deploy/nginx-sportspub.conf /etc/nginx/sites-available/sportspub.conf
sudo sed -i 's/<YOUR_DOMAIN>/swyp-pub.duckdns.org/g' /etc/nginx/sites-available/sportspub.conf
sudo ln -s /etc/nginx/sites-available/sportspub.conf /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

# Let's Encrypt 발급 (HTTP-01)
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d swyp-pub.duckdns.org --agree-tos --no-eff-email -m <YOUR_EMAIL>

# 자동 갱신 확인 (cron/timer 자동 등록됨)
sudo systemctl status certbot.timer
```

## 6. DuckDNS IP 자동 갱신 (EC2 재시작 대비)

```bash
mkdir -p ~/duckdns && cd ~/duckdns
echo 'echo url="https://www.duckdns.org/update?domains=swyp-pub&token=<TOKEN>&ip=" | curl -k -o ~/duckdns/duck.log -K -' > duck.sh
chmod 700 duck.sh
(crontab -l 2>/dev/null; echo "*/5 * * * * ~/duckdns/duck.sh >/dev/null 2>&1") | crontab -
```

## 7. AWS 보안그룹

| SG | 인바운드 | 소스 |
|----|----------|------|
| EC2 | 22 (SSH) | 본인 IP /32 |
| EC2 | 80, 443 | 0.0.0.0/0 |
| RDS | 5432 | EC2 SG 만 |

RDS 는 private subnet 권장, public access 비활성.

## 운영 명령 치트시트

```bash
sudo systemctl restart sportspub-backend    # 재시작
sudo systemctl status sportspub-backend     # 상태
sudo journalctl -u sportspub-backend -f     # 로그 tail
sudo tail -f /var/log/sportspub/app.log     # 파일 로그
sudo nginx -s reload                        # nginx 재로드
sudo certbot renew --dry-run                # 인증서 갱신 테스트
```
