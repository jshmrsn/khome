
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.dokka") version "1.6.10"
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("de.jansauer.printcoverage") version "2.0.0"
    jacoco
    id("com.github.dawnwords.jacoco.badge") version "0.2.0"
}

group = "com.dennisschroeder"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

val ktor_version: String by project
val koinVersion: String by project
val mockkVersion: String by project
val jupiterVersion: String by project
val assertKVersion: String by project
val dataBobVersion: String by project
val jsonAssertVersion: String by project
val kotlinLoggingVersion: String by project
val slf4jVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion") {
        exclude(group = "org.mockito")
        exclude(group = "junit")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
    implementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")
    testImplementation("org.skyscreamer:jsonassert:$jsonAssertVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks {
    val dokkaHtml by getting(DokkaTask::class)

    dokkaHtml {
        outputDirectory.set(rootDir.resolve("docs"))
    }
}

defaultTasks("dokkaHtml")

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

tasks.withType<Test> {
    environment["HOST"] = "home-assistant.local"
    environment["PORT"] = 8321
    environment["ACCESS_TOKEN"] = "dsq7zht54899dhz43kbv4dgr56a8we234h>!sg?x"
    environment["SECURE"] = true
    environment["START_STATE_STREAM"] = false
    useJUnitPlatform()
}

tasks {
    check {
        dependsOn(test)
        finalizedBy(jacocoTestReport, jacocoTestCoverageVerification, printCoverage, generateJacocoBadge)
    }

    jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
        }
    }
}

detekt {
    input = files("$projectDir/src/main/kotlin")
    config = files("$projectDir/config/detekt-config.yml")
}

ktlint {
    version.set("0.44.0")
    ignoreFailures.set(false)
}

jacoco {
    toolVersion = "0.8.4"
}

printcoverage {
    coverageType.set("LINE")
}
