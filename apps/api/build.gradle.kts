buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.4")
        classpath("org.flywaydb:flyway-database-postgresql:10.20.1")
    }
}

plugins {
    java
    id("org.springframework.boot") version "3.4.11"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "10.20.1"

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

extra["springAiVersion"] = "1.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    runtimeOnly("org.postgresql:postgresql:42.7.8")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // JWT issue/verify
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

flyway {
    url = "jdbc:postgresql://localhost:5433/youtube_tool"
    user = "postgres"
    password = "Postgres"
    locations = arrayOf("classpath:db/migration")
    schemas = arrayOf("public")
    cleanDisabled = false
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
