plugins {
    id("java")
    id("jacoco")
    id("org.sonarqube") version "4.2.0.3129"
    id("org.jetbrains.intellij") version "1.12.0"
}

group = "ca.etsmtl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<Test> {
        useJUnitPlatform()
        // fixes 0% coverage bug
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
        finalizedBy(jacocoTestReport)
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    sonar {
        properties {
            property("sonar.projectKey", "pfe032_leakage-analysis")
            property("sonar.organization", "pfe032")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }

    named("sonar").configure {
        dependsOn(test)
    }
}