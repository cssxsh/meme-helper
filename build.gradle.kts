plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "xyz.cssxsh.meme-helper"
version = "1.0.0-dev"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.2.3")
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.0")
    // Test
    testImplementation(kotlin("test", "1.6.21"))
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.20")
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.20")
    testRuntimeOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.0")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}