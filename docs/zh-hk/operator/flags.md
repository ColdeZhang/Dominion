# 權限可見性配置

## 簡介

Dominion 開發的初衷是僅用於防熊與保護領地，隨著使用人數的增加，有些服務器管理員希望可以增加例如「禁止怪物生成」這樣的權限。
在作者看來這樣的權限屬於利用插件功能改變遊戲內容，因此這類權限默認情況是不可見、不生效且玩家無法配置的。

通過 flags.yml 服務器管理員可以打開一些默認關閉的權限，讓玩家可以在遊戲內配置這些權限。

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

- enable：是否啟用此權限；
- default_value：權限的默認值；

## 註意事項

- 如果關閉了某個權限，那麽此權限將不會出現在遊戲內 TUI 權限列表中，並且無論默認值如何， Dominion 都不會再對此權限的對應行為作處理；
- 修改默認值不會對已有的權限造成影響，只會對修改之後的新建領地生效；
- 此文件支持熱修改，無需重啟服務器或者重載插件，只需要使用 `/dom reload_config` 指令重新加載配置文件即可；
