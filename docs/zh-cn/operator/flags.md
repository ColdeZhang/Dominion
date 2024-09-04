# 权限可见性配置

## 简介

Dominion 开发的初衷是仅用于防熊与保护领地，随着使用人数的增加，有些服务器管理员希望可以增加例如“禁止怪物生成”这样的权限。
在作者看来这样的权限属于利用插件功能改变游戏内容，因此这类权限默认情况是不可见、不生效且玩家无法配置的。

通过 flags.yml 服务器管理员可以打开一些默认关闭的权限，让玩家可以在游戏内配置这些权限。

## 配置文件
```yaml
privilege:
  anchor:
    default: false
    enable: true
  animal_killing:
    default: false
    enable: true
    ...
environment:
  animal_spawn:
    default: true
    enable: false
  animal_move:
    default: true
    enable: false
    ...
```

- enable：是否启用此权限；
- default_value：权限的默认值；

## 注意事项

- 如果关闭了某个权限，那么此权限将不会出现在游戏内 TUI 权限列表中，并且无论默认值如何， Dominion 都不会再对此权限的对应行为作处理；
- 修改默认值不会对已有的权限造成影响，只会对修改之后的新建领地生效；
- 此文件支持热修改，无需重启服务器或者重载插件，只需要使用 `/dom reload_config` 指令重新加载配置文件即可；