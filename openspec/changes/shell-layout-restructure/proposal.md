## Why

当前主界面布局过于简单，只有顶部导航栏和中间 TabPane 区域，缺乏典型的企业级应用布局结构。为了支持后续功能扩展（如侧边导航菜单、状态栏、品牌展示等），需要重构为标准的上中下三区域布局，并预留侧边栏空间。

## What Changes

- **新增 header 三区域布局**：左侧品牌区域（图标+文字）、中间弹性占位区域、右侧系统用户区域
- **新增 aside 侧边栏区域**：宽度 240px，暂时放置导航菜单按钮（首页/表单/列表），后续重构为数据驱动的导航栏
- **新增 footer 底部区域**：展示版权信息
- **移动导航菜单按钮**：从 header 移至 aside 侧边栏
- **移除返回按钮**：header 中的 btnBack 按钮
- **新增系统按钮**：header 右侧新增 btnSystem 按钮（空实现占位）
- **新增品牌图标**：PNG/SVG 文件展示在 header 左侧

## Capabilities

### New Capabilities

- `shell-layout`: 主界面布局结构规范，定义 header/main/footer 三区域及 aside/content 布局
- `brand-display`: 品牌展示功能，图标+文字组合展示在 header 左侧
- `footer-display`: 底部版权信息展示

### Modified Capabilities

无（这是新增布局结构，不改变现有功能的行为规范）

## Impact

- `shell.fxml`: 重构布局结构
- `ShellController.java`: 移除 btnBack，新增 brandIcon/brandLabel/btnSystem/copyrightLabel，调整菜单按钮逻辑
- `base.css`, `light.css`, `dark.css`: 新增各区域样式
- `messages_zh_CN.properties`, `messages_en_US.properties`: 新增文案
- `src/main/resources/images/logo.png`: 新增品牌图标文件
