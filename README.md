# Conpick Backend

Spring Boot 기반 Conpick 백엔드 API 서버.

## 기술 스택

- Java 17, Spring Boot 4.1
- Spring Security + OAuth2 Client (Google 로그인)
- JWT (jjwt)
- Spring Data JPA, MySQL

## 로컬 실행

### 1. 사전 준비

- MySQL 실행 중이어야 함 (`localhost:3306`, DB 이름 `mydb`)
- Google Cloud Console에 OAuth 클라이언트 등록
  - 승인된 리디렉션 URI: `http://localhost:8080/login/oauth2/code/google`

### 2. 환경 변수

프로젝트 루트에 `.env` 파일 생성 (git에 올리지 않음):

```properties
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=   # HS512용, 64바이트 이상
```

### 3. 실행

```bash
./gradlew bootRun
```

서버: `http://localhost:8080`

## 문서

- [인증(Google OAuth2 + JWT)](docs/auth.md): 로그인 흐름, 파일별 역할, 설정, 알려진 이슈

## 브랜치

- `main`: 배포 가능한 상태
- `feature/*`: 기능 개발 후 PR로 `main`에 병합

## 참고 문서
- youtube API V3: https://developers.google.com/youtube/v3/guides/auth/server-side-web-apps?_gl=1*13mulgx*_up*MQ..*_ga*MTc3NjcxNzQ0OC4xNzkxNTY2NTI4*_ga_SM8HXJ53K2*czE3OTE1NjY1MjgkbzEkZzAkdDE3OTE1NjY1MjgkajYwJGwwJGgw&hl=ko
