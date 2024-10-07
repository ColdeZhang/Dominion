# 开发者文档

> DominionAPI 自 Dominion-2.9.0-beta 开始支持。

## 一、接入 DominionAPI

### 1. 引入依赖

如果您使用 gradle，可以在您的 `build.gradle` 文件中添加如下代码：

```groovy
// build.gradle
repositories {
    maven { url = "https://ssl.lunadeer.cn:14454/repository/maven-snapshots/" }
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:2.0-SNAPSHOT")
}
```

或者你使用的是 gradle kotlin dsl：

```kotlin
// build.gradle.kts
repositories {
    maven("https://ssl.lunadeer.cn:14454/repository/maven-snapshots/")
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:2.0-SNAPSHOT")
}
```

再或者您使用 maven，可以在您的 `pom.xml` 文件中添加如下代码：

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
    <version>2.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
</dependencies>
```

### 2. plugin.yml 配置

在您的插件的 `plugin.yml` 文件中添加如下配置，以确保在 Dominion 准备完成后再加载您的插件：

```yaml
# plugin.yml
depend: [ Dominion ]
```

## 二、使用 DominionAPI

可以通过如下方法直接获取 DominionAPI 实例：

```java
import cn.lunadeer.dominion.api.Dominion;
import cn.lunadeer.dominion.api.DominionAPI;

DominionAPI dominionAPI = Dominion.getInstance();
```

例如，获取某个位置的领地信息：

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
[示例项目地址](https://github.com/ColdeZhang/DominionPluginExample)。
