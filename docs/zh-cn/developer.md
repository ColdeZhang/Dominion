# 开发者文档

[![Javadoc](https://img.shields.io/badge/Javadoc-Link-70f3ff?logo=readthedocs)](https://coldezhang.github.io/DominionAPI/)
[![示例插件](https://img.shields.io/badge/示例插件-GitHub-blue?logo=github)](https://github.com/ColdeZhang/DominionAddonExample)

> DominionAPI 自 Dominion-2.9.0-beta 开始支持。

## 一、接入 DominionAPI

### 1. 引入依赖

如果您使用 gradle，可以在您的 `build.gradle` 文件中添加如下代码：

```groovy
// build.gradle
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:3.5")
}
```

或者你使用的是 gradle kotlin dsl：

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("cn.lunadeer:DominionAPI:3.5")
}
```

再或者您使用 maven，可以在您的 `pom.xml` 文件中添加如下代码：

```xml
<!-- pom.xml -->

<dependencies>
    <dependency>
        <groupId>cn.lunadeer</groupId>
        <artifactId>DominionAPI</artifactId>
        <version>3.5</version>
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
import cn.lunadeer.dominion.api.DominionAPI;

DominionAPI dominionAPI = DominionAPI.getInstance();
```

例如，获取某个位置的领地信息：

```java

@Override
public void onEnable() {
    // Plugin startup logic
    try {
        DominionAPI dominionAPI = DominionAPI.getInstance();
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
