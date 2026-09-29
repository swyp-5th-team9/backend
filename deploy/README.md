# Moball Backend 운영 배포 가이드

EC2 (Ubuntu 22.04) + RDS (PostgreSQL 16) + Nginx + DuckDNS + Let's Encrypt 기준.

> 크롤러(KBO 스케줄 동기화) 는 EC2 상주 대신 GitHub Actions cron 로 실행합니다 → [crawler-cron.md](./crawler-cron.md)

## 사전 준비
- AWS 계정 (EC2 t2.micro 프리티어 가능, RDS는 db.t4g.micro)
- DuckDNS 서브도메인 + 토큰 (`https://www.duckdns.org`)
- 카카오/네이버 OAuth 키 (선택)

## 빠른 시작 (자동화 스크립트)

`scripts/` 디렉토리의 sh 4종으로 수기 단계를 줄일 수 있습니다.

```bash
# EC2 안에서 repo clone 후
cd backend/deploy/scripts

# 1) 초기 셋업 (Java/Nginx 설치, moball 유저·디렉토리·systemd 등록)
./01_bootstrap.sh

# 2) /etc/moball/moball.env 작성 (DB/JWT/OAuth 값)
sudo cp ../.env.example /etc/moball/moball.env
sudo nano /etc/moball/moball.env
sudo chown root:moball /etc/moball/moball.env
sudo chmod 640 /etc/moball/moball.env

# 3) 로컬에서 빌드한 jar 받아서 배포 (jar 이름은 build.gradle 에서 moball-app.jar 로 고정)
#    (로컬) ./gradlew :app:bootJar && scp app/build/libs/moball-app.jar <EC2>:/tmp/app.jar
./02_deploy.sh

# 4) HTTPS 셋업 (도메인이 EC2 IP 가리키도록 DuckDNS 설정 먼저)
./03_https_setup.sh moball.duckdns.org <YOUR_EMAIL>

# 5) DuckDNS 자동 IP 갱신 cron 등록
./04_duckdns_cron.sh moball <DUCKDNS_TOKEN>
```

---

## 수기 단계 (참고용)

자동화 스크립트가 무엇을 하는지 알고 싶을 때 참고.

### 1. EC2 초기 셋업

```bash
sudo apt update
sudo apt install -y openjdk-21-jre-headless nginx

sudo useradd -r -s /usr/sbin/nologin moball
sudo mkdir -p /opt/moball /etc/moball /var/log/moball
sudo chown -R moball:moball /opt/moball /var/log/moball
sudo chmod 750 /etc/moball
```

### 2. 환경 변수 파일

```bash
sudo cp deploy/.env.example /etc/moball/moball.env
sudo nano /etc/moball/moball.env
sudo chown root:moball /etc/moball/moball.env
sudo chmod 640 /etc/moball/moball.env
```

### 3. systemd 등록

```bash
sudo cp deploy/moball-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable moball-backend
```

### 4. JAR 배포

```bash
# 로컬 (jar 산출물 이름은 build.gradle 에서 moball-app.jar 로 고정 — 버전 무관)
./gradlew :app:bootJar
scp app/build/libs/moball-app.jar ec2:/tmp/app.jar

# EC2
sudo mv /tmp/app.jar /opt/moball/app.jar
sudo chown moball:moball /opt/moball/app.jar
sudo systemctl restart moball-backend
sudo journalctl -u moball-backend -f
```

### 5. Nginx + HTTPS

```bash
sudo cp deploy/nginx-moball.conf /etc/nginx/sites-available/moball.conf
sudo sed -i 's/<YOUR_DOMAIN>/moball.duckdns.org/g' /etc/nginx/sites-available/moball.conf
sudo ln -s /etc/nginx/sites-available/moball.conf /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d moball.duckdns.org --agree-tos --no-eff-email -m <YOUR_EMAIL>

sudo systemctl status certbot.timer
```

### 6. DuckDNS IP 자동 갱신

```bash
mkdir -p ~/duckdns && cd ~/duckdns
echo 'echo url="https://www.duckdns.org/update?domains=moball&token=<TOKEN>&ip=" | curl -k -o ~/duckdns/duck.log -K -' > duck.sh
chmod 700 duck.sh
(crontab -l 2>/dev/null; echo "*/5 * * * * ~/duckdns/duck.sh >/dev/null 2>&1") | crontab -
```

### 7. AWS 보안그룹

| SG | 인바운드 | 소스 |
|----|----------|------|
| EC2 | 22 (SSH) | 본인 IP /32 |
| EC2 | 80, 443 | 0.0.0.0/0 |
| RDS | 5432 | EC2 SG 만 |

RDS는 private subnet 권장, public access 비활성.

---

## 운영 명령 치트시트

```bash
sudo systemctl restart moball-backend    # 재시작
sudo systemctl status moball-backend     # 상태
sudo journalctl -u moball-backend -f     # 로그 tail
sudo tail -f /var/log/moball/app.log     # 파일 로그
sudo nginx -s reload                     # nginx 재로드
sudo certbot renew --dry-run             # 인증서 갱신 테스트
crontab -l                               # cron 확인
```
