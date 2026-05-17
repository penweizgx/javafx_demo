## Context

当前 shell.fxml 使用简单的 BorderPane 结构，只有 top（顶部导航栏）和 center（TabPane）两个区域。需要重构为更完整的企业级应用布局，支持后续功能扩展。

现有结构：
```
StackPane (root)
└── BorderPane
    ├── top: HBox (导航按钮 + 用户操作)
    └── center: TabPane
```

## Goals / Non-Goals

**Goals:**
- 实现上中下三区域布局（header/main/footer）
- 实现 main 区域的左右分栏（aside/content）
- Header 支持三区域水平布局（品牌/弹性占位/系统用户）
- 预留 aside 侧边栏空间供后续导航菜单重构
- 保持现有功能（Tab 管理、主题切换、用户信息展示）

**Non-Goals:**
- 不实现数据驱动的导航菜单结构（后续单独变更）
- 不实现系统按钮的具体功能（仅占位）
- 不改变 TabPane 的多标签页逻辑

## Decisions

### 1. 布局容器选择：BorderPane 嵌套

**决策**: 使用 BorderPane 作为主要布局容器，center 区域嵌套另一个 BorderPane 实现 aside + content。

**理由**:
- BorderPane 天然支持 top/center/bottom 三区域
- 嵌套 BorderPane 可轻松实现 center 区域的 left/center 分栏
- 与现有结构兼容，改动最小

**备选方案**:
- VBox + HBox 组合：需要更多嵌套层级，布局计算复杂
- GridPane：不适合这种简单的区域划分场景

### 2. Aside 宽度：固定 240px

**决策**: Aside 侧边栏使用固定宽度 240px，不响应窗口大小变化。

**理由**:
- 侧边导航栏通常固定宽度，提供稳定的视觉锚点
- 240px 是常见的侧边栏宽度，适合图标+文字菜单

### 3. Header 三区域实现：HBox + Region 弹性占位

**决策**: Header 使用 HBox，中间用 Region + HBox.hgrow="ALWAYS" 实现弹性占位。

**理由**:
- 简单直接，JavaFX 标准做法
- 右侧功能扩展时自动挤占中间空间

### 4. 品牌图标：PNG 文件 + ImageView

**决策**: 品牌图标使用 PNG 文件，通过 ImageView 展示。

**理由**:
- PNG 格式兼容性好，无需额外依赖
- 可轻松替换图标文件
- ImageView 支持尺寸控制（fitWidth/fitHeight）

**备选方案**:
- Ikonli 图标库：需要添加 Maven 依赖，增加复杂度
- SVG 文件：JavaFX 支持，但 PNG 更通用

### 5. 导航按钮选中样式：CSS 类切换

**决策**: 导航按钮选中状态通过 CSS 类 `menu-selected` 切换实现。

**理由**:
- 与现有实现一致，改动最小
- CSS 类切换性能好，易于维护

## Risks / Trade-offs

### [Risk] 品牌图标文件缺失导致显示空白
→ **Mitigation**: 在代码中检查图标文件是否存在，不存在时仅显示文字

### [Risk] Aside 宽度过大影响小屏幕体验
→ **Mitigation**: 后续可考虑添加侧边栏折叠功能（本次不实现）

### [Risk] 布局改动影响现有 Controller 逻辑
→ **Mitigation**: 保持 TabPane 和菜单按钮的核心逻辑不变，仅调整 UI 结构和样式

## Open Questions

无（设计方案已明确）
