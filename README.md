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

## GitHub Packages 발행

기본 개발 버전은 `0.1.0-SNAPSHOT`입니다. 정식 버전은 GitHub Release를 발행할 때
Release 태그에서 결정합니다. 예를 들어 `v0.1.0` Release를 발행하면 GitHub Actions가
테스트를 통과한 JAR을 다음 좌표로 발행합니다.

```text
com.ktcloud.travelplanner:travel-common:0.1.0
```

발행에는 저장소의 `GITHUB_TOKEN`을 사용하므로 별도 발행 토큰을 Secrets에 저장할 필요가 없습니다.
Release 태그는 `vMAJOR.MINOR.PATCH` 형식이어야 합니다.

## 서비스에서 사용

Identity와 Community는 GitHub Packages 저장소와 인증 정보를 추가한 뒤 정식 버전에 의존합니다.

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/protove/travel-common")
        credentials {
            username = providers.gradleProperty("gpr.user")
                .orElse(providers.environmentVariable("GITHUB_ACTOR"))
                .orNull
            password = providers.gradleProperty("gpr.key")
                .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                .orNull
        }
    }
    mavenCentral()
}

dependencies {
    implementation("com.ktcloud.travelplanner:travel-common:0.1.0")
}
```

로컬 개발자는 개인 토큰(classic)의 `read:packages` 권한을 사용하며,
저장소 안이 아닌 `~/.gradle/gradle.properties`에 다음 값을 둡니다.

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_CLASSIC_PERSONAL_ACCESS_TOKEN
```

GitHub Actions에서 다른 서비스가 패키지를 읽을 때는 `travel-common` 패키지 설정에서
해당 서비스 저장소에 Actions 접근 권한을 부여하고, 워크플로에 `packages: read` 권한을 설정합니다.
