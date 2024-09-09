plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.lunadeer"
version = "2.6.2-beta"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// utf-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.mikeprimm.com/")
        maven("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.opencollab.dev/main/")
    }

    dependencies {
        compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.6.2")
        compileOnly("us.dynmap:DynmapCoreAPI:3.4")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")

        implementation("cn.lunadeer:MinecraftPluginUtils:1.3.8-SNAPSHOT")
        implementation("org.yaml:snakeyaml:2.0")
    }

    tasks.processResources {
        outputs.upToDateWhen { false }
        // replace @version@ in plugin.yml with project version
        filesMatching("**/plugin.yml") {
            filter {
                it.replace("@version@", rootProject.version.toString())
            }
        }
    }

    tasks.shadowJar {
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        dependsOn(tasks.withType<ProcessResources>())
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":v1_20_1"))
    implementation(project(":v1_21"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}

tasks.register("buildPlugin") { // <<<< RUN THIS TASK TO BUILD PLUGIN
    dependsOn(tasks.clean)
    dependsOn(tasks.shadowJar)
}