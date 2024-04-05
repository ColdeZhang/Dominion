<div style="text-align: center;">

<img src="https://ssl.lunadeer.cn:14437/i/2024/03/28/6604f0cec0f0e.png" alt="" width="70%">

### [开源地址](https://ssl.lunadeer.cn:14446/zhangyuheng/Dominion) | [文档地址](https://ssl.lunadeer.cn:14448/doc/23/)

</div>

## 简介

鉴于 Residence 插件的作者项目较多，维护压力大，无法及时跟进新版本以及适配Folia核心。故开发此插件，旨在平替纯净版生存服Residence的使用。

请注意，本插件仍然处于测试阶段，因此可能存在一定的行为控制漏洞。如果遇到此类遗漏的行为控制欢迎即使发送邮件或添加QQ告知，感激不尽。

## 说明

本插件基本还原了Residence的核心功能，主要适用于原版纯净生存服务器的防破坏目的，因此暂不考虑引入价格系统、商店等非原版Minecraft玩法。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf3b08c986b.png)

为了提高存储效率，本插件使用了数据库+缓存的方式存储领地数据，玩家配置领地权限直接修改数据库内容，随后触发缓存更新。权限控制则以异步的方式访问缓存，减少事件阻塞。

权限系统主要由领地权限、玩家特权组成，玩家特权优先级高于领地权限。没有特权的玩家在领地内收到领地权限的控制，有特权配置则按照特权设置受控。

## 功能介绍

- 使用 Postgresql 存储数据；
- 支持BlueMap卫星地图渲染；
- 可视化领地权限配置；
- 支持为玩家单独设置特权；
- 支持设置领地管理员；
- 支持子领地；

## 支持版本

- 1.20.1+ (Paper、Folia)

## 安装方法

1. 将插件放入服务器的 `plugins` 目录下
2. 重启服务器
3. 在 `plugins/Dominion/config.yml` 中配置
4. 重启服务器

## 玩家使用方法

### 1. 创建领地

领地有两种创建方式：1.手动选择区域创建；2.以操作者为中心自动创建。

手动创建：需要使用箭矢作为选区工具，依次使用左键点选领地长方体区域的第一个点、右键点击长方体区域的第二个点。然后使用`/dominion create <领地名称>`
创建领地，领地名称不可与其他领地重复。

自动创建：不需要选择对角线点，会以玩家为中心自动创建一定区域的领地。使用 `/dominion auto_create <领地名称>`即可自动创建领地区域。

### 2. 领地管理

使用`/dominion menu`可以打开领地系统的可视化操作文字界面（TUI），单击【我的领地】可以查看自己创建的所有领地。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf80216464b.png)

单击对应领地的【管理】即可进入对应领地的管理界面。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf807884b37.png)

单击【权限设置】即可配置其他玩家在领地内的行为控制。

![](/media/202402/2024-02-16_233445_1258510.4401325488988944.png)

绿色打勾表明启用，红色方框表示关闭。单击可以切换对应权限的开关状态。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf80dcd4b4d.png)

### 3. 玩家特权

在领地管理界面单击【玩家权限】即可管理玩家在此领地内的特权，请注意：玩家特权的优先级要高于领地权限控制。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf830224cde.png)

默认没有玩家拥有特权，可以点击【选择玩家创建特权】选择玩家创建其特权，注意：此处只会显示登录过服务器的玩家名称，因此暂时不支持对从没有在服务器登录过的玩家进行操作。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf82f10c88e.png)

选择了对应玩家后即可管理玩家在此领地的特权。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf84498fc4c.png)

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf84600f24d.png)

同样可以点击【清除】，即可删除此玩家在此领地的所有特权。

管理员：你可以在玩家特权中将一个玩家设置为【管理员】，那么此玩家即可拥有所有特权，同时此玩家还会**拥有此领地的权限编辑权限**
，但是无法对领地大小进行编辑，同时也无法设置其他玩家为管理员。

### 4. 领地范围编辑

总体而言对范围进行编辑有扩大与缩小两个操作。

扩大：面向想要扩大的方向，使用命令`/dominion expand [大小] [领地名称]`。

缩小：面向想要缩小的方向，使用命令`/dominion contract [大小] [领地名称]`。

其中大小和领地名称都是可选的，如果不填写，则大小默认为10，领地默认为当前所在领地。

请注意：1.如果需要填写领地名则同时需要填写大小；2.一般不建议在领地外进行扩大缩小操作；3.当对子领地进行操作时需要指明子领地名称。

### 5. 子领地

创建方法与普通领地相同，可以使用自动创建，也可以手动创建。

命令分别为：

`/dominion create_sub <子领地名称> [父领地名称]`

`/dominion auto_create_sub <子领地名称> [父领地名称]`

当不填写父领地名称时会尝试以当前所在领地为父领地进行创建。

当玩家处在一个子领地内时，其行为只收到子领地的权限控制，即权限大小为：

玩家子领地特权 > 子领地 > 父领地特权 > 父领地

## 管理员指南

## 指令

以下指令尖括号`<>`表示必填参数，方括号`[]`表示可选参数。

### 玩家指令

| 指令名        | 指令                                                          |
|------------|-------------------------------------------------------------|
| 打开交互菜单     | `/dominion menu`                                            |
| 查看帮助       | `/dominion help [页码]`                                       |
| 创建领地       | `/dominion create <领地名称>`                                   |
| 自动创建领地     | `/dominion auto_create <领地名称>`                              |
| 创建子领地      | `/dominion create_sub <子领地名称> [父领地名称]`                      |
| 自动创建子领地    | `/dominion auto_create_sub <子领地名称> [父领地名称]`                 |
| 管理领地       | `/dominion manage <领地名称>`                                   |
| 扩张领地       | `/dominion expand [大小] [领地名称]`                              |
| 缩小领地       | `/dominion contract [大小] [领地名称]`                            |
| 设置进入领地的提示语 | `/dominion set_enter_msg <提示语> [领地名称]`                      |
| 设置离开领地的提示语 | `/dominion set_leave_msg <提示语> [领地名称]`                      |
| 重命名领地      | `/dominion rename <原领地名称> <新领地名称>`                          |
| 转让领地       | `/dominion give <领地名称> <玩家名称>`                              |
| 删除领地       | `/dominion delete <领地名称>`                                   |
| ---------- | ----------                                                  |
| 列出所有领地     | `/dominion list`                                            |
| 查看领地信息     | `/dominion info [领地名称]`                                     |
| 查看领地权限信息   | `/dominion flag_info <领地名称> [页码]`                           |
| 设置领地权限     | `/dominion set <权限名称> <true/false> [领地名称]`                  |
| 创建玩家特权     | `/dominion create_privilege <玩家名称> [领地名称]`                  |
| 设置玩家特权     | `/dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]` |
| 重置玩家特权     | `/dominion clear_privilege <玩家名称> [领地名称]`                   |
| 查看领地玩家特权列表 | `/dominion privilege_list [领地名称] [页码]`                      |
| 查看玩家特权信息   | `/dominion privilege_info <玩家名称> [领地名称] [页码]`               |

### 管理员指令

| 指令名  | 指令                        |
|------|---------------------------|
| 重载缓存 | `/dominion reload_cache`  |
| 重载配置 | `/dominion reload_config` |

## 配置文件参考

```yaml
Database:
  Type: sqlite # pgsql, sqlite
  Host: localhost
  Port: 5432
  Name: dominion
  User: dominion
  Pass: dominion

# -1 表示不开启
AutoCreateRadius: 10

# -1 表示不限制
Limit:
  MinY: -64
  MaxY: 320
  SizeX: 128
  SizeY: 64
  SizeZ: 128
  Amount: 10
  Depth: 3      # 子领地深度 0：不允许子领地 -1：不限制
  WorldBlackList: [ ]

# -1 表示不开启
AutoCleanAfterDays: 180

BlueMap: true

Debug: false
```

## TODO

- WebUI
- Admin TUI

## 建议与反馈

Mail: [zhangyuheng@lunadeer.cn](mailto:zhangyuheng@lunadeer.cn)

QQ: 2751268851
