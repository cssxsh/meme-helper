plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.12.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh"
version = "1.1.0"

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
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.2.4")
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.2")
    compileOnly("xyz.cssxsh:weibo-helper:1.5.2")
    compileOnly("xyz.cssxsh:bilibili-helper:1.6.0")
    compileOnly("net.mamoe:mirai-core-utils:2.12.0")
    // Test
    testImplementation(kotlin("test", "1.6.21"))
    testImplementation("xyz.cssxsh.mirai:mirai-skia-plugin:1.1.2")
    testImplementation("net.mamoe:mirai-core-utils:2.12.0")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}