# Developer Documentation.

> DominionAPI has been supported since Dominion-2.9.0-beta.

## 一、Access DominionAPI.

### 1. To include dependencies.

If you are using gradle, you can add the following code to your `build.gradle` file:

```groovy
// build.gradle
repositories {
    maven { url = "https://ssl.lunadeer.cn:14454/repository/maven-snapshots/" }
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:1.4-SNAPSHOT")
}
```

or if you are using gradle kotlin dsl:

```kotlin
// build.gradle.kts
repositories {
    maven("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:1.4-SNAPSHOT")
}
```

or if you are using maven, you can add the following code to your `pom.xml` file:

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
    <version>1.4-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
</dependencies>
```

### 2. plugin.yml configuration

Add the following configuration to your plugin's plugin.yml file to ensure that your plugin loads after Dominion is ready:

```yaml
# plugin.yml
depend: [ Dominion ]
```

## 二、Using the DominionAPI.

You can directly obtain an instance of DominionAPI by following these methods:

```java
import cn.lunadeer.dominion.api.Dominion;
import cn.lunadeer.dominion.api.DominionAPI;

DominionAPI dominionAPI = Dominion.getInstance();
```

For example, to obtain the territory information of a certain location:

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
