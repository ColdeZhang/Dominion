# 指令列表

> 以下指令 '<>' 表示必填参数，'[]' 表示可选参数

## 领地管理

| 功能            | 指令                                                                              | 备注                                             |
|---------------|---------------------------------------------------------------------------------|------------------------------------------------|
| 创建领地          | `/dominion create <领地名称>`                                                       |
| 自动创建领地        | `/dominion auto_create <领地名称>`                                                  |
| 创建子领地         | `/dominion create_sub <子领地名称> [父领地名称]`                                          | 如果不指定父领地名称，则默认为当前所在领地；                         |
| 自动创建子领地       | `/dominion auto_create_sub <子领地名称> [父领地名称]`                                     | 如果不指定父领地名称，则默认为当前所在领地；                         |
| 列出领地列表        | `/dominion list`                                                                |
| 管理领地          | `/dominion manage <领地名称>`                                                       |
| 设置领地访客权限      | `/dominion set <权限名称> <true/false> [领地名称]`                                      | 如果不指定领地名称，则默认为当前所在领地；                          |
| 朝着视角方向扩张领地    | `/dominion expand [size=10] [face=NORTH,SOUTH,EAST,WEST,UP,DOWN] [name=领地名称]`   | 如果不指定大小，则默认为10；如果不指定领地名称，则默认为当前所在领地；           |
| 朝着视角方向缩小领地    | `/dominion contract [size=10] [face=NORTH,SOUTH,EAST,WEST,UP,DOWN] [name=领地名称]` | 如果不指定大小，则默认为10；如果不指定领地名称，则默认为当前所在领地；           |
| 删除领地          | `/dominion delete <领地名称>`                                                       |
| 设置进入领地的提示消息   | `/dominion set_enter_msg <提示语> [领地名称]`                                          | 如果不指定领地名称，则默认为当前所在领地；                          |
| 设置离开领地的提示消息   | `/dominion set_leave_msg <提示语> [领地名称]`                                          | 如果不指定领地名称，则默认为当前所在领地；                          |
| 设置领地的传送点      | `/dominion set_tp_location [领地名称]`                                              | 设置领地的传送点为当前位置；如果不指定领地名称，则默认为当前所在领地；            |
| 重命名领地         | `/dominion rename <原领地名称> <新领地名称>`                                              |
| 转让领地给其他玩家     | `/dominion give <领地名称> <玩家名称>`                                                  |
| 设置领地在卫星地图上的颜色 | `/dominion set_map_color <颜色> [领地名称]`                                           | 颜色需要是十六进制颜色值，如 `#ff0000`；如果不指定领地名称，则默认为当前所在领地； |

## 成员管理

| 功能     | 指令                                                            | 备注 |
|--------|---------------------------------------------------------------|----|
| 添加成员   | `/dominion member add <领地名称> <玩家名称>`                          |
| 移除成员   | `/dominion member remove <领地名称> <玩家名称>`                       |
| 列出领地成员 | `/dominion member list <领地名称>`                                |
| 设置成员权限 | `/dominion member set_flag <领地名称> <玩家名称> <权限名称> <true/false>` |
| 应用权限模板 | `/dominion member apply_template <领地名称> <玩家名称> <模板名称>`        |

## 权限组管理

| 功能      | 指令                                                            | 备注 |
|---------|---------------------------------------------------------------|----|
| 创建权限组   | `/dominion group create <领地名称> <权限组名称>`                       |
| 删除权限组   | `/dominion group delete <领地名称> <权限组名称>`                       |
| 添加权限组成员 | `/dominion group add_member <领地名称> <权限组名称> <玩家名称>`            |
| 移除权限组成员 | `/dominion group remove_member <领地名称> <权限组名称> <玩家名称>`         |
| 设置权限组权限 | `/dominion group set_flag <领地名称> <权限组名称> <权限名称> <true/false>` |
| 列出领地权限组 | `/dominion group list <领地名称>`                                 |

## 杂项

| 功能       | 指令                                                       | 备注                    |
|----------|----------------------------------------------------------|-----------------------|
| 传送到领地    | `/dominion tp <领地名称>`                                    |
| 查看领地信息   | `/dominion info [领地名称]`                                  | 如果不指定领地名称，则默认为当前所在领地； |
| 创建权限模板   | `/dominion template create <模板名称>`                       |
| 删除权限模板   | `/dominion template delete <模板名称>`                       |
| 列出权限模板   | `/dominion template list`                                |
| 设置权限模板   | `/dominion template set_flag <模板名称> <权限名称> <true/false>` |
| 从 res 迁移 | `/dominion migrate <res领地名称>`                            | 需要管理员启用领地迁移才能使用；      |
| 使用权限组称号  | `/dominion use_title <权限组ID>`                            | 需要管理员启用权限组称号才能使用；     |

## 管理员指令

| 功能        | 指令                        | 备注                      |
|-----------|---------------------------|-------------------------|
| 重载配置文件    | `/dominion reload_config` | 绝大多数配置修改后都需要重载配置文件才能生效； |
| 重载缓存      | `/dominion reload_cache`  | 有些时候可以解决一些问题；           |
| 导出（备份）数据库 | `/dominion export_db`     | 将数据库导出为文件，便于迁移、备份等操作；   |
| 从文件导入数据库  | `/dominion import_db`     | 将数据库导入为文件，便于迁移、备份等操作；   |