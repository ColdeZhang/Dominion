
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.lunadeer"
version = "1.44.1-beta"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// utf-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    mavenLocal()
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

    implementation("cn.lunadeer:MinecraftPluginUtils:1.3.4-SNAPSHOT")
    implementation("org.yaml:snakeyaml:2.0")
}

tasks {
    processResources {
        // replace @version@ in plugin.yml with project version
        filesMatching("**/plugin.yml") {
            filter {
                it.replace("@version@", project.version.toString())
            }
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        dependsOn(processResources)
    }
}
