plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
    id ("org.sonarqube") version "7.3.0.8198"
    checkstyle
}

sonar {
    properties {
        property ("sonar.projectKey", "artem90s_java-project-99")
        property ("sonar.organization", "artem90s")
    }
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
