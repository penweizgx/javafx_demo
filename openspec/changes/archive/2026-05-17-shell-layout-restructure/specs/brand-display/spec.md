## ADDED Requirements

### Requirement: Brand display with icon and text
品牌区域 SHALL 展示图标和文字的组合，图标位于文字左侧。

#### Scenario: Brand icon and text visible
- **WHEN** 主界面 Header 显示
- **THEN** 品牌区域可见图标（PNG/SVG 图片）
- **AND** 图标右侧显示品牌名称文字

### Requirement: Brand icon size
品牌图标 SHALL 具有固定尺寸，适配 Header 高度。

#### Scenario: Icon size appropriate
- **WHEN** 品牌图标显示
- **THEN** 图标尺寸为 24x24 像素
- **AND** 图标与文字垂直居中对齐

### Requirement: Brand text styling
品牌名称文字 SHALL 具有醒目的样式，与 Header 背景形成对比。

#### Scenario: Brand text readable
- **WHEN** Header 使用深色背景（亮色主题）
- **THEN** 品牌文字为白色
- **AND** 文字字体加粗显示
