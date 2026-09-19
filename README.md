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
| 最大等级 | 1 |

手持附魔剑**右键**即可召唤飞剑并自动骑乘，召唤时剑消耗 1 点耐久。

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
| 最大等级 | 4 |

手持附魔剑**左键点击空气**发射剑气。

- 伤害 = `(玩家攻击力 + 武器附魔伤害加成) × (等级 × 0.25)`
- 每命中一个目标后，后续伤害 × 0.8
- 冷却 = `80 − 等级 × 13` 刻
- 飞行速度 = `1.0 + 等级 × 0.15`
- 存在时间 = `12 + 等级` 刻
- 满级（4 级）可穿透多个目标，未满级命中即消失
- 每次释放有 50% 概率消耗 1 点耐久

### 安装

1. 安装 Minecraft 1.20.1 与对应版本的 Forge（47.x）
2. 将 `flysword-1.2.0.jar` 放入 `.minecraft/mods`
3. 启动游戏

也可以直接用指令附魔：

```
/give @s diamond_sword{Enchantments:[{id:"flysword:flysword",lvl:1},{id:"flysword:swordbeam",lvl:4}]}
```

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

### 许可与致谢

本项目以 **GPL-3.0** 授权发布。

剑气相关的实体与渲染代码改编自 coolAlias 的
[Dynamic Sword Skills](https://github.com/coolAlias/DynamicSwordSkills)（同为 GPL-3.0）。
