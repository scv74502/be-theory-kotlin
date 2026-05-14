# week1-TDD

Spring Boot + Kotlin 기반의 TDD 학습용 단일 모듈 프로젝트.

이 문서는 프로젝트의 현재 사실과 요구사항을 기록한다.
개발 방식, DDD 경계, 테스트 작성 규칙은 `docs/groundRules.md` 를 따른다.

## 기술 스택

| 분류 | 항목 | 버전 |
| --- | --- | --- |
| 언어 | Kotlin (JVM) | 1.9.25 |
| 런타임 | Java Toolchain | 21 |
| 빌드 | Gradle (Kotlin DSL) | 8.14.4 (wrapper) |
| 프레임워크 | Spring Boot | 3.5.14 |
| 의존성 관리 | io.spring.dependency-management | 1.1.7 |
| ORM | Spring Data JPA (Hibernate) | Spring Boot 관리 |
| DB | H2 (runtime) | Spring Boot 관리 |
| API 문서 | springdoc-openapi (webmvc-ui) | 2.8.16 |
| 직렬화 | jackson-module-kotlin | Spring Boot 관리 |
| Validation | spring-boot-starter-validation | Spring Boot 관리 |
| 테스트 | spring-boot-starter-test, kotlin-test-junit5, MockK, JUnit Platform Launcher | Spring Boot 관리 / MockK 1.13.13 |

### Kotlin 컴파일러/플러그인 설정
- `kotlin("plugin.spring")` — Spring 컴포넌트 클래스 자동 `open`
- `kotlin("plugin.jpa")` — JPA 엔티티 no-arg 생성자
- `allOpen` 대상 어노테이션: `jakarta.persistence.Entity`, `MappedSuperclass`, `Embeddable`
- `freeCompilerArgs`: `-Xjsr305=strict`
- 테스트 러너: `useJUnitPlatform()`

## 프로젝트 메타
- `group`: `org.sampletask`
- `version`: `0.0.1-SNAPSHOT`
- `rootProject.name`: `week1-tdd`
- 기본 패키지: `org.sampletask.week1tdd`

## 모듈 구조

단일 모듈(루트 프로젝트, 서브 모듈 없음).
```
week1-TDD/
├── build.gradle.kts          # 빌드/의존성 정의
├── settings.gradle.kts       # 루트 프로젝트 설정
├── gradle/wrapper/           # Gradle 8.14.4 wrapper
├── gradlew, gradlew.bat
└── src/
    ├── main/
    │   ├── kotlin/org/sampletask/week1tdd/
    │   │   ├── Week1TddApplication.kt           # @SpringBootApplication 진입점
    │   │   ├── common/
    │   │   │   ├── constant/AuthHeaders.kt      # 인증 헤더 상수
    │   │   │   ├── error/HttpStatusException.kt # HttpStatus 기반 공통 예외
    │   │   │   └── web/ApiExceptionHandler.kt   # 전역 API 예외 핸들러
    │   │   ├── domain/user/
    │   │   │   ├── application/
    │   │   │   │   ├── port/UserRepository.kt   # 사용자 조회/저장 포트
    │   │   │   │   ├── UserRegisterService.kt   # 사용자 회원가입 유스케이스
    │   │   │   │   └── UserQueryService.kt      # 사용자 인증/조회 유스케이스
    │   │   │   ├── infrastructure/persistence/
    │   │   │   │   ├── UserEntity.kt            # 사용자 JPA Entity
    │   │   │   │   ├── UserJpaRepository.kt     # Spring Data JPA Repository
    │   │   │   │   └── UserPersistenceAdapter.kt # application port 구현
    │   │   │   ├── presentation/
    │   │   │   │   ├── controller/UserController.kt
    │   │   │   │   ├── controller/UserRegisterController.kt
    │   │   │   │   ├── dto/request/RegistUserRequest.kt
    │   │   │   │   └── dto/response/RetrieveCurrentUserResponse.kt
    │   │   │   ├── model/User.kt                # 애그리거트 루트
    │   │   │   ├── vo/{Birthday,Email,LoginId,Name,Password}.kt
    │   │   │   └── port/PasswordEncoder.kt      # 도메인 아웃바운드 포트
    │   │   ├── infrastructure/security/
    │   │   │   ├── BCryptPasswordEncoder.kt   # PasswordEncoder 구현(Spring Security 위임)
    │   │   │   └── SecurityConfig.kt                  # PasswordEncoder 빈 정의
    │   └── resources/
    │       ├── application.properties           # spring.application.name=week1-TDD
    │       ├── static/
    │       └── templates/
    └── test/
        └── kotlin/org/sampletask/week1tdd/
            ├── Week1TddApplicationTests.kt      # @SpringBootTest 컨텍스트 로드
            ├── domain/user/
            │   ├── UserTestSteps.kt             # 도메인 루트 픽스처 (object + default param 빌더)
            │   ├── application/{UserQueryServiceTest,UserRegisterServiceTest}.kt
            │   ├── model/UserTest.kt
            │   └── vo/{Birthday,Email,LoginId,Name,Password}Test.kt
            └── domain/user/presentation/
                ├── controller/{UserApiTest,UserRegisterApiTest}.kt
                └── dto/response/RetrieveCurrentUserResponseTest.kt
```

## 자주 쓰는 명령

```bash
./gradlew bootRun        # 애플리케이션 실행
./gradlew test           # 테스트 실행 (JUnit Platform)
./gradlew build          # 빌드 + 테스트
./gradlew bootJar        # 실행 가능한 JAR 생성
```

## 참고
- Swagger UI: `springdoc-openapi-starter-webmvc-ui` 의존성으로 기본 경로 `/swagger-ui.html` 사용 가능.
- H2는 `runtimeOnly`로만 포함 — 별도 `spring.datasource` 설정 없이 임베디드 모드로 동작.

## 설계/테스트 규칙 위치

- DDD 아키텍처 규칙, JPA Entity 와 POJO 도메인 객체 분리 원칙은 `docs/groundRules.md` 의 "아키텍처 규칙 (DDD)" 절을 따른다.
- 테스트 종류, 파일명 접미사, 디렉토리 정책, 픽스처(`*TestSteps`) 위치, 테스트 명명 규약, 모킹 정책, 의미 없는 테스트 회피 원칙은 `docs/groundRules.md` 의 "테스트 규약" 절을 따른다.

## 인증 헤더 규약

유저 정보가 필요한 모든 요청은 아래 두 헤더로 식별/인증한다.

| 헤더 | 의미 |
| --- | --- |
| `X-Loopers-LoginId` | 로그인 아이디 |
| `X-Loopers-LoginPw` | 로그인 비밀번호 (평문, 서버에서 인코딩 후 매칭) |

- **회원가입 (`POST /users` 등 가입 API)**: 가입 시점에는 계정이 없으므로 **request body** 로 `loginId`, `password`, `name`, `birthday`, `email` 전달. 위 헤더는 사용하지 않는다.
- **그 외 사용자 정보 필요 API**: 반드시 `X-Loopers-LoginId`, `X-Loopers-LoginPw` 헤더로 인증한다. 누락/불일치 시 `401`.
- 비밀번호는 저장 시 BCrypt 로 단방향 인코딩되며, 매칭은 `PasswordEncoder.matches(raw, encoded)` 로 수행한다.

## 사용자 정보 조회 API

### `GET /users/me`

로그인한 사용자의 정보를 반환한다.

#### Request

| 위치 | 이름 | 필수 | 의미 |
| --- | --- | --- | --- |
| Header | `X-Loopers-LoginId` | Y | 로그인 아이디 |
| Header | `X-Loopers-LoginPw` | Y | 평문 비밀번호 |

- 조회 API이므로 request body 는 사용하지 않는다.
- request DTO 는 body 가 있는 API에서 생성한다.

#### Response `200 OK`

```json
{
  "loginId": "user01",
  "name": "홍길*",
  "birthday": "1996-01-01",
  "email": "user@example.com"
}
```

- `loginId`: 영문/숫자 4~20자.
- `name`: 마지막 글자를 `*`로 마스킹한다. 1글자 이름은 `*`로 반환한다.
- `birthday`: ISO-8601 날짜 문자열(`yyyy-MM-dd`).
- `email`: 도메인 Email VO와 같은 간이 RFC 5322 형식.

#### Error

| 조건 | 상태 |
| --- | --- |
| 로그인ID 헤더 누락 | `401 Unauthorized` |
| 비밀번호 헤더 누락 | `401 Unauthorized` |
| 사용자 없음 | `401 Unauthorized` |
| 비밀번호 불일치 | `401 Unauthorized` |

API 예외 응답 상태는 Spring 표준 `HttpStatus` 로 표현한다.

## 회원가입 API

### `POST /users`

신규 사용자를 가입시킨다.

#### Request

```json
{
  "loginId": "user01",
  "password": "Abcd123!",
  "name": "홍길동",
  "birthday": "1996-01-01",
  "email": "user@example.com"
}
```

- request DTO 는 `presentation/dto/request/RegistUserRequest` 를 사용한다.
- `loginId`: 영문/숫자 4~20자, 이미 가입된 값은 사용할 수 없다.
- `password`: 8~16자, 영문 대문자/소문자/숫자/특수문자 각 1개 이상, 생년월일 토큰(`yyyyMMdd`/`yyMMdd`/`MMdd`) 포함 불가.
- `name`: 공백이 아닌 1~50자.
- `birthday`: ISO-8601 날짜 문자열(`yyyy-MM-dd`), 과거 날짜만 허용.
- `email`: 도메인 Email VO와 같은 간이 RFC 5322 형식.

#### Response `201 Created`

응답 body 는 사용하지 않는다.

#### Error

| 조건 | 상태 |
| --- | --- |
| 요청 필드 포맷 오류 | `400 Bad Request` |
| 비밀번호에 생년월일 포함 | `400 Bad Request` |
| 이미 가입된 로그인ID | `409 Conflict` |

## 도메인 규칙 — 사용자 (User)

| 필드 | 타입 | 제약 |
| --- | --- | --- |
| 로그인ID | `LoginId` | 영문/숫자 4~20자, 시스템 전체에서 **Unique** |
| 비밀번호 | `Password` | 평문 8~16자, 영문 대문자/소문자/숫자/특수문자 각 1개 이상, **생년월일 포함 불가** (yyyyMMdd/yyMMdd/MMdd) — 저장은 BCrypt 인코딩 |
| 이름 | `Name` | 공백 제외 1~50자 |
| 생년월일 | `Birthday` | `LocalDate`, 과거 날짜만 허용 |
| 이메일 | `Email` | RFC 5322 간이 형식 |

- 도메인 루트 패키지: `org.sampletask.week1tdd.domain.user`
- 애그리거트 루트: `model/User`
- 비밀번호: `Password` VO 가 평문 규칙 검증과 인코딩 생성 책임을 보유하며, 생성 후에는 인코딩된 값만 보관.
- 암호화 포트: `port/PasswordEncoder` ↔ BCrypt 어댑터(인프라)
- 회원가입 진입점: `User.register(loginId, rawPassword, name, birthday, email, encoder)`
- 로그인ID 유일성은 도메인 객체 자체로는 보장 불가 → `UserRegisterService` 에서 Repository 사전 조회로 강제
