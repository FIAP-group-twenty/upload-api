plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.9.25'
    id 'org.jetbrains.kotlin.plugin.spring' version '1.9.25'
    id 'org.springframework.boot' version '3.4.2'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.jetbrains.kotlin.plugin.jpa' version '1.9.25'
    id("org.sonarqube") version "6.0.1.5171"
    id("jacoco")
}

group = 'br.com.soat'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

jacoco {
    toolVersion = "0.8.8"
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'com.fasterxml.jackson.module:jackson-module-kotlin'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'
    implementation 'org.jetbrains.kotlin:kotlin-reflect'
    implementation 'com.amazonaws:aws-java-sdk-s3:1.12.780'

//    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3'


    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit5'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll '-Xjsr305=strict'
    }
}

allOpen {
    annotation 'jakarta.persistence.Entity'
    annotation 'jakarta.persistence.MappedSuperclass'
    annotation 'jakarta.persistence.Embeddable'
}

sonarqube {
    properties {
        property ("sonar.projectKey", "group-twenty_upload-api")
        property ("sonar.organization", "group-twenty")
        property ("sonar.host.url", project.findProperty("SONAR_HOST_URL") ?: "")
        property("sonar.login", project.findProperty("SONAR_TOKEN") ?: "")
        property("sonar.kotlin.language.level", "1.9")
        property("sonar.sources", "src/main/kotlin")
        property("sonar.tests", "src/test/kotlin")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()

    finalizedBy(tasks.jacocoTestReport)
    testLogging {
        events("passed", "failed", "skipped")
    }
    reports {
        junitXml.required.set(true)
        junitXml.outputLocation.set(file("${project.projectDir}/test-results/test"))
        html.required.set(true)
        html.outputLocation.set(file("${project.projectDir}/test-results/test"))
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(file("${project.projectDir}/reports/jacoco"))
    }
}
