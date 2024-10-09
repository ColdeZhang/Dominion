# 配置文件参考

```yaml
Database:
  Type: sqlite # pgsql, sqlite, mysql
  Host: localhost
  Port: '5432'
  Name: dominion
  User: dominion
  Pass: dominion
  
# 语言设置，参考 languages 文件夹下的文件名
Language: zh-cn

# 自动创建领地的半径，单位为方块
# -1表示不开启
AutoCreateRadius: 10

# 默认进入领地提示消息
DefaultJoinMessage: '&3{OWNER}: Welcome to {DOM}!'
# 默认离开领地提示消息
DefaultLeaveMessage: '&3{OWNER}: Leaving {DOM}...'

# 领地提示消息显示位置（BOSS_BAR, ACTION_BAR, TITLE, SUBTITLE, CHAT）
MessageDisplay:
  # 玩家没有权限时的提示消息位置
  NoPermission: ACTION_BAR
  # 进入/离开领地时的提示消息位置
  JoinLeave: SUBTITLE

# 默认玩家圈地限制
Limit:
  SpawnProtection: 10 # 出生点保护半径 出生点此范围内不允许圈地-1表示不开启
  MinY: -64 # 最小Y坐标
  MaxY: 320 # 最大Y坐标
  Size:
    MaxX: 628 # X方向最大长度-1表示不限制
    MaxY: 64 # Y方向最大长度-1表示不限制
    MaxZ: 628 # Z方向最大长度-1表示不限制
    MinX: 4 # X方向最小长度
    MinY: 4 # Y方向最小长度
    MinZ: 4 # Z方向最小长度
  Amount: 10 # 最大领地数量-1表示不限制
  Depth: 3 # 子领地深度0表示不开启-1表示不限制
  Vert: false # 是否自动延伸到 MaxY 和 MinY
  OpByPass: true # 是否允许OP无视领地限制
  WorldSettings:
    some_world_name:
      MinY: -64
      MaxY: 320
      SizeX: 128
      SizeY: 64
      SizeZ: 128
      Amount: 10
      Depth: 3
      Vert: false

Teleport:
  Enable: true
  Delay: 0 # 传送延迟 秒
  CoolDown: 0 # 传送冷却 秒
  
# 自动清理长时间未上线玩家的领地（天）
# -1表示不开启
AutoCleanAfterDays: 180

# 圈地工具名称
Tool: ARROW

# 经济设置
# 需要安装 Vault 前置及插件
Economy:
  Enable: false
  Price: 10.0 # 圈地价格 单位每方块
  OnlyXZ: false # 是否只计算xz平面积
  Refund: 0.85 # 删除或缩小领地时的退款比例
  
# 飞行权限节点 - 拥有以下任意一个权限节点的玩家不会被本插件拦截飞行
FlyPermissionNodes:
  - essentials.fly
  - cmi.command.fly

# 是否允许玩家从 Residence 迁移领地数据
ResidenceMigration: false

# 权限组称号 - 使用权限组当作称号(需要PlaceholderAPI插件)
# 变量: %dominion_group_title%
# 前后缀如需要加颜色请使用这种格式 &#ffffff
GroupTitle:
  Enable: false
  Prefix: '['
  Suffix: ']'

BlueMap: false
Dynmap: false

CheckUpdate: true

Debug: false

Timer: false # 性能测试计时器

```

## 配置说明

### Database

可选数据库，Postgresql、Sqlite3，`1.33.4-beta` 开始支持Mysql。

- 如果使用 Postgresql 数据库，需要手动创建数据库。

- 如果使用 sqlite 数据库，插件会自动在插件目录下创建数据库文件。配置文件内的 Host、Port、User、Pass 字段不会被使用。

### Language

语言设置，参考 languages 文件夹下的文件名。

如果需要更新语言文件，请删除 `plugins/Dominion/languages` 文件夹下对应的文件，然后重启服务器。
插件会自动生成最新的对应语言文件。

### AutoCreateRadius

配置玩家在使用“自动创建”功能时会自动向XYZ三个方向延伸此距离创建领地。

### DefaultJoinMessage & DefaultLeaveMessage

默认进入领地提示消息和默认离开领地提示消息。

### MessageDisplay

配置提示消息显示位置，可选项：`BOSS_BAR`, `ACTION_BAR`, `TITLE`, `SUBTITLE`, `CHAT`。

- NoPermission：玩家没有权限时的提示消息位置
- JoinLeave：进入/离开领地时的提示消息位置

### Limit

玩家使用此插件的一些限制：
- SpawnProtection：出生点半径保护，此半径范围内普通玩家无法创建领地
- MinY：领地的最小Y坐标
- MaxY：领地的最大Y坐标
- Size：领地的大小限制
  - MaxX：X方向最大长度 -1表示不限制
  - MaxY：Y方向最大长度 -1表示不限制
  - MaxZ：Z方向最大长度 -1表示不限制
  - MinX：X方向最小长度 不能小于等于0 不能大于MaxX
  - MinY：Y方向最小长度 不能小于等于0 不能大于MaxY
  - MinZ：Z方向最小长度 不能小于等于0 不能大于MaxZ
- Amount：每个玩家拥有的最大领地数量 -1表示不限制
- Depth：子领地深度、0表示不允许子领地、 -1表示不限制
- Vert：当设置为 `true` 时，玩家选择区域创建或者自动创建领地，会自动将Y向下向上延伸到MinY和MaxY
- WorldSettings：单独设置某个世界的圈地规则（如不设置则使用上述默认规则）
- OpByPass：是否允许OP无视领地限制

> 您服务器世界的名称应该避免使用 `default` 这样的特殊单词，否则会导致不可预料的意外错误。

### Teleport

领地传送功能，可以配置是否允许使用传送、传送延迟、两次传送之间的冷却时间。

### AutoCleanAfterDays

配置数据自动清理，-1表示不开启。180表示，如果一个玩家超过180天没有上线，那么会自动删除此玩家在本插件内的所有数据（包括他的领地、他在其他玩家领地内的权限等）。

### Tool

配置手动圈地时的选取工具。如果配置错误会被设置为默认值“ARROW”箭矢。

### Economy

经济控制支持，让玩家需要花费金钱圈地。使用此特性需要安装 Vault 经济前置插件。

- Enable：控制是否启用此功能，如果不需要请关闭。（修改是否启用需要完整重启服务器，不要使用管理插件热重载）
- Price：每个单位方块价值。
- OnlyXZ：是否只计算平面面积价值（忽略Y轴）。
- Refund：删除、缩小领地时的退还金钱比例。

### FlyPermissionNodes

飞行权限节点，拥有此列表任意一个权限节点的玩家不会被本插件拦截飞行。

EssentialX飞行权限：essentials.fly
CMI飞行权限：cmi.command.fly

### ResidenceMigration

是否允许玩家从 Residence 迁移领地数据，打开后玩家可以自行决定要从 Residence 中迁移哪些数据到 Dominion。

### GroupTitle

- Enable：是否启用权限组称号
- Prefix：称号前缀 （如需要加颜色请使用这种格式 &#ffffff）
- Suffix：称号后缀 （如需要加颜色请使用这种格式 &#ffffff）

关于如何配置详见[权限组称号](../manage-dominion/group-title.md)。

### BlueMap

配置是否在 BlueMap 渲染玩家领地。

### Dynmap

配置是否在 Dynmap 渲染玩家领地。

### CheckUpdate

自动检查更新。

### Debug

调试模式，如果遇到bug，可以尝试打开此开关后复现问题，然后将日志发送给我。