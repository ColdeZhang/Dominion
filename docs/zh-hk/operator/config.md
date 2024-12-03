# 配置文件參考

```yaml
Database:
  Type: sqlite # pgsql, sqlite, mysql
  Host: localhost
  Port: '5432'
  Name: dominion
  User: dominion
  Pass: dominion
  
# 語言設置，參考 languages 文件夾下的文件名
Language: zh-cn

# 自動創建領地的半徑，單位為方塊
# -1表示不開啟
AutoCreateRadius: 10

# 默認玩家圈地限製
Limit:
  SpawnProtection: 10 # 出生點保護半徑 出生點此範圍內不允許圈地-1表示不開啟
  MinY: -64 # 最小Y坐標
  MaxY: 320 # 最大Y坐標
  SizeX: 128 # X方向最大長度-1表示不限製
  SizeY: 64 # Y方向最大長度-1表示不限製
  SizeZ: 128 # Z方向最大長度-1表示不限製
  Amount: 10 # 最大領地數量-1表示不限製
  Depth: 3 # 子領地深度0表示不開啟-1表示不限製
  Vert: false # 是否自動延伸到 MaxY 和 MinY
  WorldBlackList: # 不允許圈地的世界列表
    - some_world
  OpByPass: true # 是否允許OP無視領地限製

Teleport:
  Enable: true
  Delay: 0 # 傳送延遲 秒
  CoolDown: 0 # 傳送冷卻 秒
  
# 自動清理長時間未上線玩家的領地（天）
# -1表示不開啟
AutoCleanAfterDays: 180

# 圈地工具名稱
Tool: ARROW

# 經濟設置
# 需要安裝 Vault 前置及插件
Economy:
  Enable: false
  Price: 10.0 # 圈地價格 單位每方塊
  OnlyXZ: false # 是否只計算xz平面積
  Refund: 0.85 # 刪除或縮小領地時的退款比例
  
# 飛行權限節點 - 擁有以下任意一個權限節點的玩家不會被本插件攔截飛行
FlyPermissionNodes:
  - essentials.fly
  - cmi.command.fly

# 是否允許玩家從 Residence 遷移領地數據
ResidenceMigration: false

# 權限組稱號 - 使用權限組當作稱號(需要PlaceholderAPI插件)
# 變量: %dominion_group_title%
# 前後綴如需要加顏色請使用這種格式 &#ffffff
GroupTitle:
  Enable: false
  Prefix: '['
  Suffix: ']'

BlueMap: false
Dynmap: false

CheckUpdate: true

Debug: false

Timer: false # 性能測試計時器

```

## 配置說明

### Database

可選數據庫，Postgresql、Sqlite3，`1.33.4-beta` 開始支持Mysql。

- 如果使用 Postgresql 數據庫，需要手動創建數據庫。

- 如果使用 sqlite 數據庫，插件會自動在插件目錄下創建數據庫文件。配置文件內的 Host、Port、User、Pass 字段不會被使用。

### Language

語言設置，參考 languages 文件夾下的文件名。

如果需要更新语言文件，请删除 `plugins/Dominion/languages` 文件夹下对应的文件，然后重启服务器。
插件会自动生成最新的对应语言文件。

### AutoCreateRadius

配置玩家在使用「自動創建」功能時會自動向XYZ三個方向延伸此距離創建領地。

### Limit

玩家使用此插件的一些限製：
- SpawnProtection：出生點半徑保護，此半徑範圍內普通玩家無法創建領地
- MinY：領地的最小Y坐標
- MaxY：領地的最大Y坐標
- SizeX：X方向最大長度
- SizeY：Y方向最大長度
- SizeZ：Z方向最大長度
- Amount：每個玩家擁有的最大領地數量
- Depth：子領地深度、0表示不允許子領地、 -1表示不限製
- WorldBlackList：不允許創建領地的世界
- Vert：當設置為 `true` 時，玩家選擇區域創建或者自動創建領地，會自動將Y向下向上延伸到MinY和MaxY。**同時也會根據 MinY 和 MaxY 的設置自動調整 SizeY 的配置保證數值邏輯一致。**

### Teleport

領地傳送功能，可以配置是否允許使用傳送、傳送延遲、兩次傳送之間的冷卻時間。

### AutoCleanAfterDays

配置數據自動清理，-1表示不開啟。180表示，如果一個玩家超過180天沒有上線，那麽會自動刪除此玩家在本插件內的所有數據（包括他的領地、他在其他玩家領地內的權限等）。

### Tool

配置手動圈地時的選取工具。如果配置錯誤會被設置為默認值「ARROW」箭矢。

### Economy

經濟控製支持，讓玩家需要花費金錢圈地。使用此特性需要安裝 Vault 經濟前置插件。

- Enable：控製是否啟用此功能，如果不需要請關閉。（修改是否啟用需要完整重啟服務器，不要使用管理插件熱重載）
- Price：每個單位方塊價值。
- OnlyXZ：是否只計算平面面積價值（忽略Y軸）。
- Refund：刪除、縮小領地時的退還金錢比例。

> 关于 Vault 兼容性：
>
> 本插件支持 Vault 和 [VaultUnlocked](https://www.spigotmc.org/resources/vaultunlocked.117277/)，其中
>
> Dominion 2.14.5-beta 及以下请使用 VaultUnlock 2.3 版本。
>
> Dominion 2.14.6-beta 及以上请使用 VaultUnlock 2.7 及以上版本。

### FlyPermissionNodes

飛行權限節點，擁有此列表任意一個權限節點的玩家不會被本插件攔截飛行。

EssentialX飛行權限：essentials.fly
CMI飛行權限：cmi.command.fly

### ResidenceMigration

是否允許玩家從 Residence 遷移領地數據，打開後玩家可以自行決定要從 Residence 中遷移哪些數據到 Dominion。

### GroupTitle

- Enable：是否啟用權限組稱號
- Prefix：稱號前綴 （如需要加顏色請使用這種格式 &#ffffff）
- Suffix：稱號後綴 （如需要加顏色請使用這種格式 &#ffffff）

關於如何配置詳見[權限組稱號](../manage-dominion/group-title.md)。

### BlueMap

配置是否在 BlueMap 渲染玩家領地。

### Dynmap

配置是否在 Dynmap 渲染玩家領地。

### CheckUpdate

自動檢查更新。

### Debug

調試模式，如果遇到bug，可以嘗試打開此開關後復現問題，然後將日誌發送給我。
