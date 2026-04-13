plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("net.ltgt.flyway") version "1.0.0"
    id("nu.studer.jooq") version "10.2"
    id("org.openapi.generator") version "7.10.0"
}

group = "com.example.agyhandson"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

springBoot {
    mainClass.set("com.example.agyhandson.AgyHandsOnApplication")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
    
    // Auth
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // DB
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    jooqGenerator("org.postgresql:postgresql")
    
    // For net.ltgt.flyway
    "flyway"("org.flywaydb:flyway-core:11.1.0")
    "flyway"("org.flywaydb:flyway-database-postgresql:11.1.0")
    "flyway"("org.postgresql:postgresql")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers:1.20.1")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
}

flyway {
    url = "jdbc:postgresql://localhost:5432/mydb"
    user = "user"
    password = "pass"
}

jooq {
    version.set("3.19.28")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/mydb"
                    user = "user"
                    password = "pass"
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                        inputSchema = "public"
                    }
                    target.apply {
                        packageName = "com.example.agyhandson.infrastructure.persistence.jooq.generated"
                        directory = "build/generated/jooq"
                    }
                }
            }
        }
    }
}

tasks.named("generateJooq") {
    dependsOn("flywayMigrate")
    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/../docs/openapi.yaml")
    outputDir.set("$projectDir/build/generated/openapi")
    apiPackage.set("com.example.agyhandson.presenter.api")
    modelPackage.set("com.example.agyhandson.presenter.model")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "useSpringBoot3" to "true",
        "useTags" to "true",
        "openApiNullable" to "false",
        "generateConstructorWithAllArgs" to "true",
        "generateConstructorWithRequiredArgs" to "true",
        "useBeanValidation" to "true",
        "performBeanValidation" to "true",
        "serializationLibrary" to "jackson",
        "enumPropertyNaming" to "original"
    ))
}

sourceSets {
    main {
        java {
            srcDir("build/generated/openapi/src/main/java")
            srcDir("build/generated/jooq")
        }
    }
}

tasks.compileJava {
    dependsOn("openApiGenerate")
}
