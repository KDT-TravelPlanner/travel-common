# travel-common

Travel Planner 서비스가 공유하는 HTTP 응답 계약과 request-id 로깅 라이브러리입니다.
Spring Boot 실행 애플리케이션이 아니며 표준 JAR을 생성합니다.

## v0.1 API

- `RequestId`, `RequestIdGenerator`
- `RequestLoggingContext`, `RequestLoggingFilter`
- `ApiResponse`
- `PatchField`
- `ApiErrorResponse`, `FieldErrorResponse`

## 로컬 발행

```shell
./gradlew clean build publishToMavenLocal
```

소비 서비스에서는 다음 좌표를 사용합니다.

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.ktcloud.travelplanner:travel-common:0.1.0-SNAPSHOT")
}
```

`RequestLoggingFilter`는 유효한 canonical UUID v4 `X-Request-Id`를 전달받으면 유지하고,
누락되었거나 유효하지 않으면 새 UUID v4를 생성합니다.

기존 로깅 필터가 있는 서비스에서는 `RequestIdGenerator`와 응답 계약만 사용합니다.
`RequestLoggingFilter`는 자동 등록되지 않으므로 새 서비스가 명시적으로 bean으로 등록할 수 있습니다.
