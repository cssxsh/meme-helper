plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"

    id("net.mamoe.mirai-console") version "2.14.0"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "xyz.cssxsh"
version = "1.2.0"

repositories {
    mavenLocal()
    mavenCentral()
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "meme-helper")
    licenseFromGitHubProject("AGPL-3.0")
    workingDir = System.getenv("PUBLICATION_TEMP")?.let { file(it).resolve(projectName) }
        ?: buildDir.resolve("publishing-tmp")
    publication {
        artifact(tasks["buildPlugin"])
    }
}

dependencies {
    compileOnly("xyz.cssxsh:bilibili-helper:1.6.6")
    compileOnly("xyz.cssxsh:weibo-helper:1.5.8")
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.7.1")
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:1.3.0")
    testImplementation(kotlin("test"))
    //
    implementation(platform("net.mamoe:mirai-bom:2.14.0"))
    compileOnly("net.mamoe:mirai-core")
    compileOnly("net.mamoe:mirai-core-utils")
    compileOnly("net.mamoe:mirai-console-compiler-common")
    testImplementation("net.mamoe:mirai-core-utils")
    //
    implementation(platform("io.ktor:ktor-bom:2.2.3"))
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-encoding")
    //
    implementation(platform("org.slf4j:slf4j-parent:2.0.6"))
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