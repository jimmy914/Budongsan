# 🌿 브랜치 전략

Git Flow 전략을 기반으로 합니다.

## 브랜치 구조

```
main
 └── develop
      ├── feature/member
      ├── feature/property
      ├── feature/map
      └── ...
```

## 브랜치 설명

| 브랜치 | 용도 | 규칙 |
|--------|------|------|
| `main` | 실제 배포 브랜치 | 직접 push 금지, PR만 허용 |
| `develop` | 개발 통합 브랜치 | feature 브랜치를 여기로 merge |
| `feature/*` | 기능 개발 브랜치 | develop에서 분기, 완료 후 develop으로 PR |
| `hotfix/*` | 긴급 버그 수정 | main에서 분기, 완료 후 main + develop으로 merge |

## 브랜치 네이밍 규칙

```
feature/기능명        # 기능 개발
feature/member        # 회원 관리
feature/property      # 매물 관리
feature/map           # 지도 연동
hotfix/버그내용        # 긴급 수정
```

## 작업 흐름

```
1. GitHub에서 이슈 생성
2. develop에서 feature 브랜치 생성
   git checkout develop
   git checkout -b feature/기능명

3. 기능 개발 후 커밋
   git add .
   git commit -m "feat: 기능 설명"

4. develop으로 PR 생성
   - 이슈 번호 연결 (close #이슈번호)
   - 코드 리뷰 후 merge

5. 배포 시 develop → main PR
```
