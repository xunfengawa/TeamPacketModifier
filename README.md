# TeamGlow-1.20.4插件使用指南

## 0. 依赖
#### ProtocolLib(1.20.4):
- https://ci.dmulloy2.net/job/ProtocolLib/679/artifact/build/libs/ProtocolLib.jar

## 1. 功能介绍
1. 可选颜色队伍: **队内发光**
2. 可选颜色队伍: **可看到全部** [功能1中队伍] **发光**

## 2. 配置文件config.yml
```
    # 队伍发光插件配置文件
    # 颜色待选列表: [black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, dark_gray,
    #              blue, green, aqua, red, light_purple, yellow, white]
    # (参考wiki格式化代码页面)

    # 是否启用TeamGlow插件
    enable:
        true

    # 只有队内发光的队伍颜色列表
    Glow-TeamColors:
        ["red", "blue", "yellow", "green", "gold", "dark_purple", "light_purple", "dark_aqua"]

    # 能看到所有发光的队伍颜色列表
    SeeAllGlow-TeamColors:
        ["white", "dark_gray"]
```

## 3. 游戏内指令/teamglow
### /teamglow on
- 启用TeamGlow功能
### /teamglow off
- 禁用TeamGlow功能
### /teamglow reload
- 重载配置文件