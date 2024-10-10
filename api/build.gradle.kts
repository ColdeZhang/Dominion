plugins {
    id("java")
    id("maven-publish")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withJavadocJar()
    withSourcesJar()
}

// utf-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "cn.lunadeer"
            artifactId = "DominionAPI"
            version = "2.0-SNAPSHOT"
            // 添加组件
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
            credentials {
                // from m2 settings.xml
                username = project.findProperty("nexusUsername")?.toString()
                password = project.findProperty("nexusPassword")?.toString()
            }
        }
    }
}
