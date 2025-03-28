plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

val junitVersion = "5.8.2"

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    
    // Add Ktor client dependencies
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    
    // Use JUnit 5 (Jupiter) for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    
    // Make sure platform versions are aligned
    testImplementation("org.junit.platform:junit-platform-commons:1.8.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.8.2")
    
    // Keep Ktor test host
    testImplementation(libs.ktor.server.test.host)
    
    // Keep kotlin.test for specific assertions
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
