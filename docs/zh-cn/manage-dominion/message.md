# 领地提示消息

## 进入消息

可以为领地设置玩家进入时的欢迎语句，提示语将在玩家进入领地时弹出。

```
/dominion set_enter_msg <提示语> [领地名称]
```

## 离开消息

可以为领地设置玩家离开时的欢送语句，提示语将在玩家离开领地时弹出。

```
/dominion set_leave_msg <提示语> [领地名称]
```

## 特殊占位符

- `{OWNER}`：会被自动替换为领地主人的名字；
- `{DOM}`：会被自动替换为领地名称；

## 提示

- 提示语支持 PlaceholderAPI 占位符，例如 `%player%`
  会被替换为玩家名，本插件支持的占位符请参考 [PlaceHolderApi 一览](../operator/papi.md)；
- 提示语支持颜色代码，例如 `&a` 会被替换为亮绿色；
- 提示语支持渐变色，更多关于颜色的效果参见 [彩色字符](https://ssl.lunadeer.cn:14448/doc/81/)。

> PlaceholderAPI 占位符需要服务器管理员安装 PlaceholderAPI 插件方可支持。
