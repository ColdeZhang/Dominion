# 特权玩家配置

如果你想给赞助玩家（或者VIP）一些特殊优惠，例如更少的圈地价格、可以圈更大的领地等，那请继续阅读下面的内容。

## 前置条件

1. Dominion 插件版本 >= 2.2.0;
2. 安装有权限插件，例如 LuckPerms；

## 配置圈地限制

假设你现在想为赞助玩家提供优惠，打开 `plugins/Dominion/groups/sponsor.yml` 文件。修改里面的设置为你期望的数值，保存文件。

假如你想再为 VIP 玩家提供更丰厚的优惠，你可以复制一份 `sponsor.yml` 重命名为 `vip.yml`
，然后编辑里面的数值。（这个文件的名称你可以自定义，但是请不要使用中文、特殊字符、空格等）

## 加载配置

使用指令 `/dom reload_config`，加载配置的权限组。

## 将相应玩家添加到权限组

以 LuckPerms 为例，打开权限组查看是否有对应的权限，按照第一步的内容这里需要用 `sponsor` 和 `vip` 两个权限组：

<img src="https://ssl.lunadeer.cn:14437/i/2024/08/20/66c46029af3ed.png" alt="" width="20%">

如果没有则需要创建，创建完成后根据你的需要给对应玩家添加 `group.sponsor` 或者 `group.vip` 即可。

最后保存 LuckPerms 配置，即可生效。

## 注意事项

`groups` 中的 `WorldSettings` 与 `config.yml` 中的是独立的。
例如：你不希望任何玩家在下届顶层圈地（包括 `sponsor`），你已经在 `config.yml` 中设置了下届的 `MinY` 和 `MaxY`。
如果你不在 `sponsor.yml` 中进行同样的设置的话，那么 `sponsor` 玩家就不会收到这个限制。

