import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

var BuildFull = properties["BuildFull"].toString() == "true"
var libraries = listOf<String>()
libraries += "org.postgresql:postgresql:42.7.2"
libraries += "mysql:mysql-connector-java:8.0.33"
libraries += "net.kyori:adventure-platform-bukkit:4.3.3"


group = "cn.lunadeer"
version = "4.0.0-alpha.10"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.codemc.org/repository/maven-public")
    }

    dependencies {
        compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.6.2")
        compileOnly("us.dynmap:DynmapCoreAPI:3.4")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("com.github.MilkBowl:VaultAPI:1.7")
        compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.7")

        if (!BuildFull) {
            libraries.forEach {
                compileOnly(it)
            }
        } else {
            libraries.forEach {
                implementation(it)
            }
        }
    }

    tasks.processResources {
        outputs.upToDateWhen { false }
        // copy languages folder from PROJECT_DIR/languages to core/src/main/resources
        from(file("${projectDir}/languages")) {
            into("languages")
        }
        // replace @version@ in plugin.yml with project version
        filesMatching("**/plugin.yml") {
            filter {
                it.replace("@version@", rootProject.version.toString())
            }
            if (!BuildFull) {
                var libs = "libraries: ["
                libraries.forEach {
                    libs += "$it,"
                }
                filter {
                    it.replace("libraries: [ ]", libs.substring(0, libs.length - 1) + "]")
                }
            }
        }
    }

    tasks.shadowJar {
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        dependsOn(tasks.withType<ProcessResources>())
        // add -lite to the end of the file name if BuildLite is true or -full if BuildLite is false
        archiveFileName.set("${project.name}-${project.version}${if (BuildFull) "-full" else "-lite"}.jar")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":v1_20_1"))
    implementation(project(":v1_21_paper"))
    implementation(project(":v1_21_spigot"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}

tasks.register("Clean&Build") { // <<<< RUN THIS TASK TO BUILD PLUGIN
    dependsOn(tasks.clean)
    dependsOn(tasks.shadowJar)
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String) // use project version as publication version
        id.set("Dominion")
        channel.set("Beta")
        changelog.set("See https://github.com/ColdeZhang/Dominion/releases/tag/v${project.version}")
        apiKey.set(System.getenv("HANGAR_TOKEN"))
        // register platforms
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                println("ShadowJar: ${tasks.shadowJar.flatMap { it.archiveFile }}")
                platformVersions.set(listOf("1.20.1-1.20.6","1.21.x"))
            }
        }
    }
}

tasks.named("publishPluginPublicationToHangar") {
    dependsOn(tasks.named("jar"))
}
