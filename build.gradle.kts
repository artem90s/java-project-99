plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
//    id("io.sentry.jvm.gradle") version "6.9.0"
    id("org.sonarqube") version "7.3.0.8198"
    checkstyle
}

sonar {
    properties {
        property ("sonar.projectKey", "artem90s_java-project-99")
        property ("sonar.organization", "artem90s")
    }
}

//sentry {
//    includeSourceContext = true
//
//    org = "artem90s"
//    projectName = "java-spring-boot"
//    authToken = System.getenv("SENTRY_AUTH_TOKEN")
//}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("com.h2database:h2")
    implementation("org.postgresql:postgresql")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:8.43.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
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
