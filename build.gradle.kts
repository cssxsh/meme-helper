plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.11.1"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh.meme-helper"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "meme-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
        artifact(tasks.getByName("buildPluginLegacy"))
    }
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.2.3")
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.0")
    compileOnly("net.mamoe:mirai-core-utils:2.11.1")
    // Test
    testImplementation(kotlin("test", "1.6.21"))
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.20")
    testImplementation("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.0")
    testImplementation("net.mamoe:mirai-core-utils:2.11.1")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}