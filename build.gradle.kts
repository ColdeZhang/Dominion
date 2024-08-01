plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.lunadeer"
version = "1.42.9-beta"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.mikeprimm.com/")
    maven("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.6.2")
    compileOnly("us.dynmap:DynmapCoreAPI:3.4")

    shadow("cn.lunadeer:MinecraftPluginUtils:1.3.4-SNAPSHOT")
    shadow("org.yaml:snakeyaml:2.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
