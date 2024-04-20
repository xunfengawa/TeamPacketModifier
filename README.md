# TeamPacketModifier(TPM)插件使用指南

## 0. 依赖
-  Minecraft 1.20.4
-  ProtocolLib 1.20.4
https://ci.dmulloy2.net/job/ProtocolLib/679/artifact/build/libs/ProtocolLib.jar

## 1. 功能介绍
1. 可选颜色队伍: **队内发光**
2. 可选颜色队伍: **可看到全部** [功能1中队伍] **发光**
3. 可开关: 是否看得见自己隐身

## 2. 配置文件config.yml
```
# 队伍发光插件配置文件
# 颜色待选列表: [black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, dark_gray,
#              blue, green, aqua, red, light_purple, yellow, white]
# (参考wiki格式化代码页面)

# 只有队内发光的队伍颜色列表
Glow-TeamColors:
  ["red", "blue", "yellow", "green", "gold", "dark_purple", "light_purple", "dark_aqua"]

# 能看到所有发光的队伍颜色列表
SeeAllGlow-TeamColors:
  ["gray"]

# 忽略的队伍颜色列表(减少卡顿)
Ignore-TeamColors:
  ["white"]
```

## 3. 计分板接口
### 创建计分板(id: TPM)
```
/scoreboard objectives add TPM dummy
```
### 设置发光开关(id: Glow)
- 0: **启用** 同队发光和旁观全体发光
- 1: **禁用** 同队发光和旁观全体发光
```
/scoreboard players set Glow TPM 1
```
### 设置自隐身开关(id: CancelSelfInvis)
- 0: 具有隐身效果时看自己 **为隐身**
- 1: 具有隐身效果时看自己 **不为隐身**
```
/scoreboard players set CancelSelfInvis TPM 1
```

## 4. 游戏内指令/teamglow
### /teamglow reload
- 重载配置文件
### /teamglow enable
- 启用TPM功能
### /teamglow diable
- 禁用TPM功能
### /teamglow RefreshTeamMap
- 更新当前队伍信息
### /teamglow stopAllGlow
- (不稳定)停止单向发光
