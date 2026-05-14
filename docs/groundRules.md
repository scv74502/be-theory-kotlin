# Ground Rules

이 문서는 앞으로 코드를 작성할 때 지켜야 할 개발·설계·테스트 규칙을 정의한다.
프로젝트의 현재 기술 스택, 패키지 현황, 도메인 요구사항은 `docs/projectInfo.md` 에 기록한다.

## 개발 규칙

### 진행 Workflow - 증강 코딩
- **대원칙**: 방향성 및 주요 의사 결정은 개발자에게 제안만 할 수 있으며, 최종 승인된 사항을 기반으로 작업을 수행한다.
- **중간 결과 보고**: AI 가 반복적인 동작을 하거나, 요청하지 않은 기능 구현 또는 테스트 삭제를 임의로 진행할 경우 개발자가 개입한다.
- **설계 주도권 유지**: AI 는 방향성에 대한 제안을 할 수 있으나, 주요 설계 변경은 개발자의 승인을 받은 후 수행한다.

### 개발 Workflow - TDD (Red > Green > Refactor)
- 모든 테스트는 3A 원칙(Arrange - Act - Assert)으로 작성한다.

#### 1. Red Phase
- 요구사항을 만족하는 실패 테스트를 먼저 작성한다.
- 테스트가 검증하려는 비즈니스 규칙을 명확히 드러낸다.

#### 2. Green Phase
- Red Phase 의 테스트가 통과할 수 있는 최소 구현을 작성한다.
- 오버엔지니어링을 금지한다.
- 테스트 요구조건 변경 없이 통과만을 위해 테스트를 수정하지 않는다.

#### 3. Refactor Phase
- 테스트가 모두 통과하는 상태에서 코드 품질을 개선한다.
- 불필요한 private 함수 추출을 지양하고, OOP 원칙에 맞게 가독성과 유지보수성을 높인다.
- unused import 제거, ktlint 적용 등 정리 작업을 수행한다.
- 비효율적 구현이 발견되면 트레이드오프를 설명한 뒤 최적화를 제안한다.

## 아키텍처 규칙 (DDD)

### 도메인 순수성
- `domain/<aggregate>/model`, `vo`, `service`, `port` 내부에는 JPA / Spring / 영속 프레임워크 import 를 두지 않는다.
- 금지 예: `jakarta.persistence.*`, `org.springframework.*`, `org.hibernate.*`
- 허용 예: 표준 라이브러리(`java.*`, `kotlin.*`), 동일 도메인 내부 타입
- 도메인 객체는 자신의 불변식을 갖는 POJO 로 유지하고, 영속·전송 어노테이션을 붙이지 않는다.

### JPA Entity 와 도메인 객체 분리
- DDD 도메인 객체와 JPA Entity 는 프로젝트 전체에서 다른 개념으로 취급한다.
- DDD 애그리거트 루트·엔티티는 `domain/<aggregate>/model/` 에 둔다.
- JPA `@Entity` 는 `domain/<aggregate>/infrastructure/persistence/**` 에만 둔다.
- JPA Entity 이름에는 `JpaEntity` 접미사를 사용한다. 예: `UserJpaEntity`
- 영속 모델이 도메인 객체를 참조/매핑하며, 도메인 객체가 영속 모델을 참조하지 않는다.
- JPA Entity 가 도메인 객체와 1:1 형태이고 SRP 를 깨지 않는 동안은 `toDomain()` / `fromDomain()` 메서드를 JPA Entity 에 둔다.
- JPA Entity 가 변환 외 책임으로 비대해지면 별도 `*EntityMapper.kt` 로 분리한다.

### 기능 패키지 구성
- 사용자, 상품처럼 개별 도메인 기능은 `domain/<aggregate>/application`, `domain/<aggregate>/infrastructure`, `domain/<aggregate>/presentation` 아래에 둔다.
- 여러 기능에서 공유하는 예외, 상수, 웹 공통 처리는 루트의 `common/**` 아래에 둔다.
- 순수 도메인 모델은 `domain/<aggregate>/model`, `vo`, `service`, `port` 에 두고, Spring/JPA/presentation 의존성을 넣지 않는다.

### 의존 방향
- 의존 방향은 외부 계층에서 도메인 쪽으로 향한다.
- 도메인은 presentation, application, infrastructure 의 구현 세부사항을 알지 않는다.
- 외부 협력자가 필요하면 도메인 또는 application 쪽에 port 인터페이스를 두고, infrastructure 에서 구현한다.

### API 예외 처리
- 전역 API 예외 핸들러는 `common/web` 에 두고, 특정 도메인 전용 예외 처리와 분리한다.
- HTTP 응답 상태를 표현하는 에러 타입은 커스텀 enum/string 대신 Spring 표준 `HttpStatus` 를 사용한다.
- 컨트롤러/유스케이스에서 인증 실패, 권한 실패, 잘못된 요청 등 HTTP 상태가 필요한 예외를 만들 때도 `HttpStatus` 를 기준으로 전달한다.

### 도메인 패키지 구성
```
domain/<aggregate>/
├── application/ # 유스케이스, 애그리거트별 port
├── infrastructure/ # 영속성, 외부 시스템 어댑터
├── presentation/ # 컨트롤러, 요청/응답 DTO
├── model/      # DDD 애그리거트 루트·엔티티(POJO)
├── vo/         # 값 객체
├── service/    # 도메인 서비스(필요할 때만 생성)
└── port/       # 외부 협력자 인터페이스
```
- `util/`, `constant/` 는 실제 추출 대상이 생길 때만 추가한다.
- 단순 편의를 위한 공용 패키지를 먼저 만들지 않는다.

## 테스트 규약

### 테스트 종류
| 종류 | 접미사 | 의도 |
| --- | --- | --- |
| 단위 | `*Test.kt` | 외부 의존 없이 빠르게 동작하는 도메인/서비스 단위 검증 |
| 통합 | `*IntegrationTest.kt` | JPA, Repository, Spring context 일부 등 외부 협력 객체와의 결합 검증 |
| API/E2E | `*ApiTest.kt` | HTTP 진입점 기준 종단 검증 (`MockMvc` / `WebTestClient` / `TestRestTemplate`) |

### 디렉토리 정책
- 현재는 `src/test` 를 기본 단위 테스트 영역으로 사용한다.
- 테스트 패키지는 main 패키지 구조와 1:1 로 미러링한다.
- 종류별 별도 디렉토리(`unit/`, `integration/`, `e2e/`)는 만들지 않는다.
- 테스트 종류는 파일명 접미사로 식별한다.
- 통합/E2E 테스트가 많아지면 별도 Gradle source set(`integrationTest`, `e2eTest`) 도입을 검토한다.

### 테스트 픽스처
- **`*TestSteps`** (도메인 객체 빌더 — 기본값 + override 파라미터)는 **도메인 루트** `domain/<aggregate>/<Aggregate>TestSteps.kt` 에 둔다. 예: `domain/user/UserTestSteps.kt`.
    - Kotlin `object` + default parameter 패턴. 메서드명은 한국어 백틱 사용 가능(예: `회원가입()`).
- **fake/stub/공용 유틸**(예: 인메모리 리포지토리, 도메인 가로지르는 헬퍼)은 같은 도메인 아래 `support/` 패키지에 둔다(필요할 때만 생성).
- 단순 호출용 헬퍼는 별도 파일로 만들지 않는다 — `*Test.kt` 내 private 함수면 충분.
- 새 테스트 추가 시 위 접미사·패키지 정책을 따른다.

### 테스트 명명 규약
- **패턴**: `` `도메인_규칙_또는_행동` `` — Kotlin 백틱 함수명 사용.
- **구성요소**:
    - 클래스명/메서드명 prefix 를 반복하지 않는다.
    - 도메인 용어를 주어로 쓰고, 허용/불가/필수/생성 같은 규칙을 문장형으로 표현한다.
    - 실패 케이스는 `예외`보다 `불가`, `필수`처럼 도메인 제약을 드러내는 표현을 우선한다.
- **예시**:
    - `` `사용자가_생성된다`() ``
    - `` `비밀번호는_생년월일_포함_불가`() ``
    - `` `생년월일은_yyyyMMdd_yyMMdd_MMdd_토큰으로_변환된다`() ``
- **`@DisplayName` 사용 금지**: 메서드명이 자체 설명적이므로 중복이다. 부가 설명이 정말 필요한 극히 예외적 케이스에만 허용한다.

### 모킹 정책
- 단위 테스트의 포트·외부 협력자 모킹은 **MockK** 로 일원화한다.
- 익명 `object : SomePort { ... }` 스텁, 한 줄짜리 anonymous override 는 금지한다(가독성·일관성 저해).
- 예외: 도메인 전체에서 재사용되는 의도적 Fake(예: 인메모리 리포지토리)는 `support/` 에 `Fake*` 명명으로 둘 수 있다.
- 의존성: `testImplementation("io.mockk:mockk:1.13.x")`

### 의미 없는 테스트 회피
다음 종류의 테스트는 작성하지 않는다(있다면 제거):
- **라이브러리 위임 어댑터**의 내부 동작 검증 — 예: `BCryptPasswordEncoder` 의 prefix/cost/salt 무작위성 검증은 Spring Security 책임 영역이다.
- **Spring DI wiring 동어반복** — `@Bean` 메서드가 리터럴로 반환하는 타입을 `is XxxAdapter` 로 다시 검증하는 행위. `@SpringBootTest` 컨텍스트 로드만으로 누락은 잡힌다.
- **표준 라이브러리·프레임워크 자체 동작**에 대한 검증.

판단 기준: "이 테스트가 깨지면 우리 코드의 어떤 회귀를 잡는가?" 에 답할 수 없으면 삭제 대상이다.
