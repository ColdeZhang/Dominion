# Developer Reference

[![Javadoc](https://img.shields.io/badge/Javadoc-Link-70f3ff?logo=readthedocs)](https://coldezhang.github.io/DominionAPI/)
[![DemoPlugin](https://img.shields.io/badge/DemoPlugin-GitHub-blue?logo=github)](https://github.com/ColdeZhang/DominionAddonExample)

> Since Dominion-2.9.0-beta, DominionAPI is supported.

## First: Import DominionAPI

### 1. Add Dependency

For gradle, you can add the following code to your `build.gradle` file:

```groovy
// build.gradle
repositories {
    maven { url = "https://ssl.lunadeer.cn:14454/repository/maven-snapshots/" }
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:3.2-SNAPSHOT")
}
```

Or if you use kotlin, you can add the following code to your `build.gradle.kts` file:

```kotlin
// build.gradle.kts
repositories {
    maven("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:3.2-SNAPSHOT")
}
```

For maven, you can add the following code to your `pom.xml` file:

```xml
<!-- pom.xml -->
<repositories>
    <repository>
        <id>lunadeer</id>
        <url>https://ssl.lunadeer.cn:14454/repository/maven-snapshots/</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>cn.lunadeer</groupId>
    <artifactId>DominionAPI</artifactId>
    <version>3.2-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
</dependencies>
```

### 2. Add Plugin Dependency

Add the following configuration to your plugin's `plugin.yml` file to ensure that your plugin is loaded after Dominion
is ready:

```yaml
# plugin.yml
depend: [ Dominion ]
```

## Second: Implement DominionAPI

Get the DominionAPI instance directly as follows:

```java
import cn.lunadeer.dominion.api.Dominion;
import cn.lunadeer.dominion.api.DominionAPI;

DominionAPI dominionAPI = Dominion.getInstance();
```

Then you can use the API, for example, to get the dominion information at a certain location:

```java

@Override
public void onEnable() {
    // Plugin startup logic
    try {
        DominionAPI dominionAPI = Dominion.getInstance();
        DominionDTO d = dominionAPI.getDominionByLoc(some_location);
        if (d == null) {
            this.getLogger().info("no dominion found");
            return;
        }
        this.getLogger().info("name:" + d.getName());
    } catch (Exception e) {
        this.getLogger().info(e.getMessage());
    }
}
```
