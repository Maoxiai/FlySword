## Fly Sword（御剑飞行）

每个人都有一个仙剑梦……

一个 Minecraft Forge 模组，为**剑**添加「御剑飞行」与「剑气」两个附魔。

### 环境要求

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x |
| Java | 17 |
| 模组版本 | 1.2.0 |

客户端与服务端都需要安装。

### 附魔

#### 御剑飞行

| 项目 | 值 |
| --- | --- |
| 注册名 | `flysword:flysword` |
| 稀有度 | 稀有（RARE） |
| 可附魔物品 | 剑 |
| 最大等级 | 5 |

手持附魔剑**右键**即可召唤飞剑并自动骑乘，召唤时剑消耗 1 点耐久。

飞行性能随附魔等级提升，1 级与旧版手感一致：

| 等级 | 水平速度倍率 | 升降加速度 |
| --- | --- | --- |
| I | 2.0 | 0.03 |
| II | 2.4 | 0.04 |
| III | 2.8 | 0.05 |
| IV | 3.2 | 0.06 |
| V | 3.6 | 0.07 |

飞行时剑身后会拖出粒子尾迹，**风格随境界递进**，粒子数量也随之增加（I 级每刻 1 个，V 级每刻 5 个）：

| 等级 | 境界 | 拖尾风格 |
| --- | --- | --- |
| I | 炼气 | 灵气符文（青绿咒文） |
| II | 筑基 | 剑罡电芒（青白电光） |
| III | 金丹 | 真元灵光（纯白光点） |
| IV | 元婴 | 幽蓝灵焰 |
| V | 化神 | 金霞仙光（金色仙尘与金光交替） |

悬停不动时不会产生尾迹。

飞行操作（按键可在「选项 → 控制」中重新绑定，条目名 **飞剑下降**）：

| 操作 | 默认按键 |
| --- | --- |
| 上升 | 空格 |
| 下降 | 左 Ctrl |
| 前后左右 | W / A / S / D |
| 收剑下马 | 左 Shift |

收剑后剑会回到背包，背包已满则掉落在脚下。飞剑实体无敌，不会受伤或掉落。

> 左 Ctrl 同时是原版的「疾跑」键，飞行时会一并触发疾跑，不影响飞行速度。

#### 剑气

| 项目 | 值 |
| --- | --- |
| 注册名 | `flysword:swordbeam` |
| 稀有度 | 稀有（RARE） |
| 可附魔物品 | 剑 |
| 最大等级 | 5 |

手持附魔剑**左键点击空气**发射剑气。

- 伤害 = `(玩家攻击力 + 武器附魔伤害加成) × (等级 × 0.25)`
- 每命中一个目标后，后续伤害 × 0.8
- 冷却 = `80 − 等级 × 13` 刻
- 飞行速度 = `1.0 + 等级 × 0.15`
- 存在时间 = `12 + 等级` 刻
- 满级（5 级）可穿透多个目标，未满级命中即消失
- 每次释放有 50% 概率消耗 1 点耐久

> 以上数值均为默认值，全部可在[配置文件](#配置)中调整。

### 安装

1. 安装 Minecraft 1.20.1 与对应版本的 Forge（47.x）
2. 将 `flysword-1.2.0.jar` 放入 `.minecraft/mods`
3. 启动游戏

也可以直接用指令附魔：

```
/give @s diamond_sword{Enchantments:[{id:"flysword:flysword",lvl:5},{id:"flysword:swordbeam",lvl:5}]}
```

### 配置

服务端与客户端都会生成 `config/flysword-common.toml`，可脱离源码调整平衡参数。

- `[fly_sword]`：水平速度倍率、升降垂直加速度、水平/垂直阻尼
- `[fly_trail]`：飞行拖尾粒子的开关与密度（风格由附魔等级决定，不可配置）
- `[sword_beam]`：冷却、伤害系数、飞行速度、存活时间、穿透衰减、击退强度、耐久消耗概率

带 `Base` / `PerLevel` 后缀的项按 `实际值 = base + (附魔等级 - 1) × perLevel` 计算。

注意：飞剑的移动由骑乘者客户端模拟，`[fly_sword]` 与 `[fly_trail]` 按各客户端本地配置生效；`[sword_beam]` 的伤害与冷却在服务端计算，以服务端配置为准。

### 从源码构建

```bash
./gradlew build
```

产物位于 `build/libs/flysword-1.2.0.jar`。

开发环境运行：

```bash
./gradlew runClient   # 启动客户端
./gradlew runServer   # 启动专用服务端
```

### 项目结构

```
src/main/java/com/flysword/
├── FlySwordMod.java        模组入口与右键/左键事件处理
├── config/                 平衡参数配置（ForgeConfigSpec）
├── enchantment/            附魔定义与注册
├── entity/                 飞剑载具、剑气投掷物
├── key/                    按键绑定
├── loader/                 实体类型注册、客户端渲染器注册
├── network/                网络包（客户端通知服务端释放剑气）
├── render/                 实体渲染器
└── utils/                  网络通道与工具类

src/main/resources/
├── META-INF/mods.toml      模组元数据
├── pack.mcmeta
└── assets/flysword/        语言文件与贴图
```

### 本地化

内置 `en_us` 与 `zh_cn`。

### 许可与版权

本项目以 **GPL-3.0** 授权发布，完整许可正文见 [LICENSE.txt](./LICENSE.txt)。

```
Copyright (C) 2026 Maoxiai
Copyright (C) 2019-2022 Kesar
```

剑气相关的实体与渲染代码改编自 coolAlias 的
[Dynamic Sword Skills](https://github.com/coolAlias/DynamicSwordSkills)（同为 GPL-3.0）。
