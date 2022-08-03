plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    id("net.mamoe.mirai-console") version "2.12.1"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "xyz.cssxsh"
version = "1.1.2"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "meme-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    workingDir = System.getenv("PUBLICATION_TEMP")?.let { file(it).resolve(projectName) }
        ?: project.buildDir.resolve("publishing-tmp")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.4.3")
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.7")
    compileOnly("xyz.cssxsh:weibo-helper:1.5.3")
    compileOnly("xyz.cssxsh:bilibili-helper:1.6.1")
    compileOnly("net.mamoe:mirai-core-utils:2.12.1")
    implementation("io.ktor:ktor-client-okhttp:2.0.3") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
    }
    implementation("io.ktor:ktor-client-encoding:2.0.3") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
    }
    implementation("com.squareup.okhttp3:okhttp:4.10.0") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
    }
    // Test
    testImplementation(kotlin("test"))
    testImplementation("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.7")
    testImplementation("net.mamoe:mirai-core-utils:2.12.1")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}