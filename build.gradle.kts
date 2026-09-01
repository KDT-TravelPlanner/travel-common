plugins {
	`java-library`
	`maven-publish`
	kotlin("jvm") version "1.9.25"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ktcloud.travelplanner"
version = providers.gradleProperty("releaseVersion")
	.orElse("0.1.0-SNAPSHOT")
	.get()
description = "Shared web contracts and request logging for Travel Planner services"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
	withSourcesJar()
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.16")
	}
}

dependencies {
	api("com.fasterxml.jackson.core:jackson-databind")
	api("jakarta.servlet:jakarta.servlet-api")
	api("org.springframework:spring-web")

	implementation("org.slf4j:slf4j-api")
	implementation("org.springframework:spring-context")

	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

publishing {
	repositories {
		maven {
			name = "GitHubPackages"
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
	}

	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = "travel-common"
			from(components["java"])

			pom {
				name.set("travel-common")
				description.set(project.description)
			}
		}
	}
}
