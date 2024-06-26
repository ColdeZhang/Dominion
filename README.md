<div style="text-align: center;">

<img src="https://ssl.lunadeer.cn:14437/i/2024/03/28/6604f0cec0f0e.png" alt="" width="70%">

### [开源地址](https://ssl.lunadeer.cn:14446/zhangyuheng/Dominion) | [文档地址](https://ssl.lunadeer.cn:14448/doc/23/)

### [下载页面](https://ssl.lunadeer.cn:14446/zhangyuheng/Dominion/releases)

### [统计页面](https://bstats.org/plugin/bukkit/Dominion/21445) | [Hangar](https://hangar.papermc.io/zhangyuheng/Dominion)

[![CodeFactor](https://www.codefactor.io/repository/github/deergiteamirror/dominion/badge/master)](https://www.codefactor.io/repository/github/deergiteamirror/dominion/overview/master)

</div>

## 简介

鉴于 Residence 插件的作者项目较多，维护压力大，无法及时跟进新版本以及适配Folia核心。故开发此插件，旨在平替纯净版生存服Residence的使用。

请注意，本插件仍然处于测试阶段，因此可能存在一定的行为控制漏洞。如果遇到此类遗漏的行为控制欢迎即使发送邮件或添加QQ告知，感激不尽。

## 说明

本插件基本还原了Residence的核心功能，主要适用于原版纯净生存服务器的防破坏目的，支持基础的价格系统。

![](https://ssl.lunadeer.cn:14437/i/2024/02/16/65cf3b08c986b.png)

为了提高存储效率，本插件使用了数据库+缓存的方式存储领地数据，玩家配置领地权限直接修改数据库内容，随后触发缓存更新。权限控制则以异步的方式访问缓存，减少事件阻塞。

权限系统主要由领地权限、玩家特权组成，玩家特权优先级高于领地权限。没有特权的玩家在领地内收到领地权限的控制，有特权配置则按照特权设置受控。

## 功能介绍

- 支持 Postgresql、Mysql、Sqlite3 存储数据；
- 支持BlueMap卫星地图渲染；
- 支持为玩家单独设置特权；
- 支持设置领地管理员；
- 支持子领地；
- 采用 TUI 方式进行权限配置交互，简单快捷；
- 支持经济系统（需要 Vault 前置）；
- 领地区域可视化；
- 管理员可在游戏内使用TUI配置领地系统；
- 支持[从 Residence 迁移](https://ssl.lunadeer.cn:14448/doc/73/)领地数据（1.33.7+）；

<div style="text-align: center;">

创建领地

<img src="https://ssl.lunadeer.cn:14437/i/2024/05/10/663debf78eca4.gif" alt="" width="60%">

权限管理

<img src="https://ssl.lunadeer.cn:14437/i/2024/05/10/663debe052786.gif" alt="" width="60%">

配置

<img src="https://ssl.lunadeer.cn:14437/i/2024/05/10/663debec11dad.gif" alt="" width="60%">


</div>

## 支持版本

- 1.20.1+ (Bukkit、Spigot、Paper、Folia)

## TODO

- WebUI

## 建议与反馈

Mail: [zhangyuheng@lunadeer.cn](mailto:zhangyuheng@lunadeer.cn)

QQ群：309428300

## 统计

![bstats](https://bstats.org/signatures/bukkit/Dominion.svg)
