# 🏠 BuDongSan

부동산 매물 관리 데스크톱 애플리케이션

## 🛠 기술 스택

### Backend
- Java 21 (LTS)
- Spring Boot 3.4
- Spring Security + JWT
- Spring Data JPA + QueryDSL
- PostgreSQL
- Redis

### Frontend
- JavaFX (데스크톱 앱)

### Infra
- Docker / Docker Compose
- GitHub Actions (CI/CD 예정)

---

## 📁 프로젝트 구조

```
budongsan/
├── budongsan-api/       # Spring Boot REST API 서버
├── budongsan-core/      # 공통 도메인, 유틸, 예외처리
├── budongsan-client/    # JavaFX 데스크톱 앱 (예정)
└── docker-compose.yml   # 로컬 개발 환경 (PostgreSQL + Redis)
```

---

## 🚀 로컬 실행 방법

### 1. 사전 준비
- Java 21
- Docker Desktop

### 2. DB 실행
```bash
docker-compose up -d
```

### 3. 서버 실행
```bash
./gradlew :budongsan-api:bootRun
```

### 4. API 문서 확인
```
http://localhost:8080/swagger-ui.html
```

---

## 🌿 브랜치 전략

[BRANCH_STRATEGY.md](docs/BRANCH_STRATEGY.md) 참고

## 📝 커밋 컨벤션

[COMMIT_CONVENTION.md](docs/COMMIT_CONVENTION.md) 참고
