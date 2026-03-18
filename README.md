# bari-backend

MSA(Microservices Architecture) + Monorepo 구조로 구성된 Spring Boot 백엔드 프로젝트입니다.
Gradle Multi-project + Version Catalog를 사용하여 여러 서비스를 하나의 레포지토리에서 관리합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.4.3 |
| 빌드 도구 | Gradle Multi-project + Version Catalog |
| API Gateway | Spring Cloud Gateway 2024.0.1 (WebFlux/Reactive) |
| 인증/인가 | Spring Security + JWT (jjwt 0.12.6) |
| 데이터베이스 | MariaDB 11.4 |
| 캐시 | Redis 7.4 |
| API 문서 | springdoc-openapi 2.8.4 (Swagger UI) |
| 메시지 큐 | Kafka (confluentinc 7.7.1), RabbitMQ 3.13 |
| 기타 | Lombok, Spring Boot Actuator, Spring Boot Validation |

---

## 프로젝트 구조

```
bari-backend/
├── settings.gradle              # 멀티 프로젝트 설정
├── build.gradle                 # 루트 빌드 설정 (공통 설정)
├── gradle/
│   └── libs.versions.toml       # Version Catalog (의존성 버전 중앙 관리)
├── .gitignore
├── README.md
├── docker/
│   ├── docker-compose.yml       # MariaDB, Redis, Kafka, RabbitMQ
│   └── sql/
│       ├── schema.sql           # 테이블 DDL
│       └── dummy-data.sql       # 테스트용 더미 데이터 (25건씩)
├── libs/                        # 공유 라이브러리 모듈
│   ├── common/                  # 공통 응답/예외 처리
│   │   └── src/main/java/com/bari/common/
│   │       ├── response/        # ApiResponse, ErrorResponse
│   │       ├── exception/       # ErrorCode(interface), BusinessException, GlobalExceptionHandler
│   │       └── entity/          # BaseTimeEntity (createdAt, deletedAt)
│   └── security/                # 인증 관련 공통 모듈
│       └── src/main/java/com/bari/security/
│           ├── jwt/             # JwtTokenProvider, JwtAuthenticationFilter
│           ├── header/          # HeaderAuthenticationFilter (X-Header 방식)
│           └── annotation/      # @CurrentUserId, CurrentUserIdArgumentResolver
└── services/                    # 마이크로서비스
    ├── api-gateway/             # 단일 진입점, JWT 검증 및 라우팅 (포트: 8080)
    ├── user-service/            # 회원가입, 로그인, JWT 발급 (포트: 8081)
    ├── store-service/           # 스토어 관리 (구현 예정)
    ├── product-service/         # 상품 관리 (구현 예정)
    ├── inventory-service/       # 재고 관리 (구현 예정)
    ├── discount-service/        # 할인 관리 (구현 예정)
    ├── order-service/           # 주문 관리 (구현 예정)
    └── item-service/            # 아이템 CRUD, X-Header 인증 예시 (포트: 8087)
```

### 모듈 설명

| 모듈 | 설명 |
|------|------|
| `libs:common` | 모든 서비스가 공유하는 공통 응답 형식, 예외 처리, BaseTimeEntity |
| `libs:security` | JWT 생성/검증, 인증 필터, @CurrentUserId 어노테이션 |
| `services:api-gateway` | 모든 외부 요청의 단일 진입점. JWT 검증 후 X-Header 주입 |
| `services:user-service` | 회원가입, 로그인, 토큰 발급/갱신/로그아웃 |
| `services:store-service` | 스토어 관리 (구현 예정) |
| `services:product-service` | 상품 관리 (구현 예정) |
| `services:inventory-service` | 재고 관리 (구현 예정) |
| `services:discount-service` | 할인 관리 (구현 예정) |
| `services:order-service` | 주문 관리 (구현 예정) |
| `services:item-service` | 아이템 CRUD. X-Header 인증 방식 사용 예시 (팀원 참고용) |

---

## 서비스별 포트

| 서비스 | 포트 | 설명 |
|--------|------|------|
| api-gateway | 8080 | 단일 진입점 (모든 외부 요청은 여기로) |
| user-service | 8081 | 사용자 인증 서비스 |
| store-service | 8082 | 스토어 서비스 (구현 예정) |
| product-service | 8083 | 상품 서비스 (구현 예정) |
| inventory-service | 8084 | 재고 서비스 (구현 예정) |
| discount-service | 8085 | 할인 서비스 (구현 예정) |
| order-service | 8086 | 주문 서비스 (구현 예정) |
| item-service | 8087 | 아이템 서비스 (팀원 예시) |
| MariaDB | 3306 | 데이터베이스 |
| Redis | 6379 | Refresh Token 저장소 |
| Kafka | 9092 | 메시지 브로커 |
| RabbitMQ | 5672 / 15672 | 메시지 큐 / 관리 콘솔 |

---

## 로컬 개발 환경 설정

### 사전 요구사항

- **Java 17** (JDK)
- **Docker** 및 **Docker Compose**
- (선택) IntelliJ IDEA 또는 VS Code

### 1. Docker 인프라 실행

```bash
# MariaDB, Redis, Kafka, RabbitMQ 실행
docker compose -f docker/docker-compose.yml up -d

# 실행 상태 확인
docker compose -f docker/docker-compose.yml ps

# MariaDB 접속 확인
docker exec -it bari-mariadb mariadb -u bari -pbari1234 bari
```

처음 실행 시 `docker/sql/schema.sql`과 `docker/sql/dummy-data.sql`이 자동으로 적용됩니다.

### DB 초기화 (스키마/더미데이터 변경 시)

`schema.sql` 또는 `dummy-data.sql`을 수정했다면 볼륨까지 삭제 후 재시작해야 변경 사항이 반영됩니다.

```bash
# 컨테이너 + 볼륨 전체 삭제 (DB 데이터 완전 초기화)
docker compose -f docker/docker-compose.yml down -v

# 다시 실행 (schema.sql + dummy-data.sql 자동 적용)
docker compose -f docker/docker-compose.yml up -d
```

> **주의:** `-v` 없이 `down`하면 볼륨은 유지되어 SQL이 다시 실행되지 않습니다.
> SQL을 변경했다면 반드시 `-v` 옵션을 붙여야 합니다.

| 명령어 | 데이터 | SQL 재실행 | 사용 시점 |
|--------|--------|-----------|----------|
| `down` | 유지 | X | 단순 재시작 |
| `down -v` | **삭제** | **O** | SQL 변경 후 초기화 |
| `restart` | 유지 | X | 컨테이너만 재시작 |

### 2. 서비스 실행

각 서비스를 별도 터미널에서 실행합니다:

```bash
# api-gateway 실행 (포트: 8080)
./gradlew :services:api-gateway:bootRun

# user-service 실행 (포트: 8081)
./gradlew :services:user-service:bootRun

# item-service 실행 (포트: 8087)
./gradlew :services:item-service:bootRun
```

로컬 프로파일 활성화 (SQL 로깅 등):

```bash
./gradlew :services:user-service:bootRun --args='--spring.profiles.active=local'
```

### 3. 빌드

```bash
# 전체 빌드
./gradlew build

# 특정 서비스만 빌드
./gradlew :services:user-service:build
```

---

## 인증 방식 설명

### JWT 발급 (user-service)

클라이언트는 로그인 시 user-service로부터 JWT Access Token과 Refresh Token을 발급받습니다.

```
클라이언트 → POST /api/auth/login → user-service(8081)
     ← { accessToken, refreshToken, userId, role }
```

- **Access Token**: 1시간 유효, 모든 API 요청 시 사용
- **Refresh Token**: 7일 유효, Redis에 저장, Access Token 만료 시 갱신용

### api-gateway를 통한 X-Header 방식

모든 외부 요청은 api-gateway(8080)를 통해 들어옵니다:

```
1. 클라이언트 → api-gateway:8080 (Authorization: Bearer {JWT})
2. JwtGatewayFilter가 JWT 검증
3. 검증 성공 → X-User-Id: {userId}, X-User-Role: {role} 헤더 추가
4. 해당 서비스로 전달 (user-service, item-service 등)
5. 각 서비스는 헤더를 신뢰하고 SecurityContext 설정
```

downstream 서비스는 JWT를 직접 검증하지 않고, api-gateway가 주입한 헤더만 사용합니다.

### 직접 서비스 호출 시 (로컬 개발/테스트)

api-gateway를 거치지 않고 직접 서비스를 호출할 때는 X-User-Id, X-User-Role 헤더를 수동으로 설정합니다:

```bash
# item-service 직접 호출 예시
curl -H "X-User-Id: 1" -H "X-User-Role: USER" http://localhost:8087/api/items

# 아이템 생성 (직접 호출)
curl -X POST \
     -H "X-User-Id: 11" \
     -H "X-User-Role: USER" \
     -H "Content-Type: application/json" \
     -d '{"name":"사과","description":"신선한 사과","price":3000}' \
     http://localhost:8087/api/items
```

---

## API 예시 (curl)

### 회원가입

```bash
curl -X POST \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"p1234","nickname":"테스트"}' \
     http://localhost:8080/api/auth/signup
```

응답:
```json
{
  "status": 201,
  "message": "생성 완료",
  "data": {
    "id": 26,
    "email": "test@example.com",
    "nickname": "테스트",
    "role": "USER",
    "createdAt": "2024-03-18T10:00:00"
  }
}
```

### 로그인

```bash
curl -X POST \
     -H "Content-Type: application/json" \
     -d '{"email":"user1@bari.com","password":"p1234"}' \
     http://localhost:8080/api/auth/login
```

응답:
```json
{
  "status": 200,
  "message": "성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 11,
    "role": "USER"
  }
}
```

### 내 정보 조회 (api-gateway 경유)

```bash
# 로그인으로 받은 accessToken 사용
ACCESS_TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -H "Authorization: Bearer $ACCESS_TOKEN" \
     http://localhost:8080/api/users/me
```

### 아이템 목록 조회 (인증 불필요)

```bash
# api-gateway 경유
curl http://localhost:8080/api/items

# item-service 직접 호출
curl http://localhost:8087/api/items
```

### 아이템 생성 (api-gateway 경유)

```bash
ACCESS_TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST \
     -H "Authorization: Bearer $ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"name":"신상 아이템","description":"설명","price":10000}' \
     http://localhost:8080/api/items
```

### X-Header로 아이템 API 직접 호출 (테스트용)

```bash
# 아이템 생성
curl -X POST \
     -H "X-User-Id: 11" \
     -H "X-User-Role: USER" \
     -H "Content-Type: application/json" \
     -d '{"name":"직접호출 아이템","price":5000}' \
     http://localhost:8087/api/items

# 아이템 수정
curl -X PUT \
     -H "X-User-Id: 11" \
     -H "X-User-Role: USER" \
     -H "Content-Type: application/json" \
     -d '{"name":"수정된 아이템","price":6000}' \
     http://localhost:8087/api/items/1

# 아이템 삭제
curl -X DELETE \
     -H "X-User-Id: 11" \
     -H "X-User-Role: USER" \
     http://localhost:8087/api/items/1
```

### 토큰 갱신

```bash
REFRESH_TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST \
     -H "Authorization: Bearer $REFRESH_TOKEN" \
     http://localhost:8080/api/auth/refresh
```

### Swagger UI

각 서비스가 실행 중이면 아래 URL에서 API 문서를 확인할 수 있습니다:

- user-service: http://localhost:8081/swagger-ui.html
- item-service: http://localhost:8087/swagger-ui.html

---

## 팀원을 위한 새 서비스/도메인 추가 가이드

### 새 서비스 추가

store, product, inventory, discount, order 서비스는 디렉토리 구조가 이미 생성되어 있습니다.
`settings.gradle` 등록도 완료된 상태이며, 아래 순서대로 코드를 채워넣으면 됩니다.

1. **Application 클래스 생성** (`src/main/java/com/bari/{domain}/`):

2. **build.gradle 작성** (item-service를 참고):
   ```groovy
   plugins {
       alias(libs.plugins.spring.boot)
       alias(libs.plugins.spring.dependency.management)
       id 'java'
   }
   dependencies {
       implementation project(':libs:security')
       implementation libs.spring.boot.web
       // ...
   }
   ```

4. **Application 클래스**:
   ```java
   @EnableJpaAuditing
   @SpringBootApplication(scanBasePackages = "com.bari")
   public class OrderServiceApplication { ... }
   ```

5. **SecurityConfig** — HeaderAuthenticationFilter 사용 (item-service 참고)

6. **WebMvcConfig** — CurrentUserIdArgumentResolver 등록 (item-service 참고)

7. **api-gateway/application.yml에 라우트 추가**:
   ```yaml
   - id: order-service
     uri: http://${ORDER_SERVICE_HOST:localhost}:${ORDER_SERVICE_PORT:8082}
     predicates:
       - Path=/api/orders/**
   ```

### @CurrentUserId 사용법

```java
// 컨트롤러에서 현재 사용자 ID 사용
@PostMapping("/orders")
public ResponseEntity<?> createOrder(@RequestBody OrderRequest request,
                                     @CurrentUserId Long userId) {
    // userId는 X-User-Id 헤더에서 자동으로 추출됩니다
    return ResponseEntity.ok(orderService.createOrder(request, userId));
}
```

### 에러 코드 추가

```java
@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELED(400, "ORDER_ALREADY_CANCELED", "이미 취소된 주문입니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### Claude 커맨드 활용

이 프로젝트에는 개발 생산성을 높이는 Claude 커맨드가 준비되어 있습니다:

- `/new-service` — 새 마이크로서비스 스캐폴딩 자동 생성
- `/new-domain` — 기존 서비스에 새 도메인(Entity, Repository, Service, Controller) 추가
- `/add-api` — 기존 컨트롤러에 새 API 엔드포인트 추가

예시:
```
/new-service name=order-service port=8082
/new-domain service=order-service domain=Payment
/add-api service=order-service controller=OrderController method=GET path=/orders/{id}
```

---

## 더미 데이터 계정

로컬 개발 시 아래 계정으로 바로 테스트할 수 있습니다 (비밀번호: `p1234`):

| 이메일 | 권한 | 설명 |
|--------|------|------|
| admin@bari.com | ADMIN | 시스템 관리자 |
| owner1@bari.com | OWNER | 가게 사장님 |
| user1@bari.com | USER | 일반 사용자 |

---

## 헬스체크

각 서비스의 헬스 상태를 확인할 수 있습니다:

```bash
curl http://localhost:8080/actuator/health  # api-gateway
curl http://localhost:8081/actuator/health  # user-service
curl http://localhost:8087/actuator/health  # item-service
```
