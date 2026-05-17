## ADDED Requirements

### Requirement: Shell layout structure
主界面 SHALL 采用上中下三区域布局结构，中间区域包含左侧边栏和右侧内容区。

#### Scenario: Layout structure verification
- **WHEN** 应用启动并显示主界面
- **THEN** 界面包含 header（顶部）、main（中间）、footer（底部）三个区域
- **AND** main 区域包含 aside（左侧边栏）和 content（右侧内容区）

### Requirement: Header three-section layout
Header 区域 SHALL 分为三个水平区域：左侧品牌区、中间弹性占位区、右侧系统用户区。

#### Scenario: Header left section contains brand
- **WHEN** 主界面显示
- **THEN** Header 左侧区域展示品牌图标和文字

#### Scenario: Header middle section is flexible
- **WHEN** Header 右侧区域添加更多功能按钮
- **THEN** 中间区域自动收缩以容纳右侧扩展内容

#### Scenario: Header right section contains system controls
- **WHEN** 主界面显示
- **THEN** Header 右侧区域包含系统按钮、主题切换按钮、用户名标签

### Requirement: Aside sidebar width
Aside 侧边栏 SHALL 具有固定宽度 240px。

#### Scenario: Sidebar fixed width
- **WHEN** 主界面显示
- **THEN** Aside 侧边栏宽度为 240px
- **AND** 侧边栏宽度不随窗口大小变化而改变

### Requirement: Aside contains navigation buttons
Aside 侧边栏 SHALL 包含导航菜单按钮，点击按钮 SHALL 在 content 区域打开对应页面。

#### Scenario: Navigation button click opens page
- **WHEN** 用户点击侧边栏中的导航按钮（首页/表单/列表）
- **THEN** content 区域显示对应页面内容
- **AND** 被点击的按钮显示选中状态样式

#### Scenario: Navigation button selection state
- **WHEN** 导航按钮被选中
- **THEN** 选中按钮具有视觉区分样式（背景色变化和左侧边框指示）
- **AND** 其他导航按钮恢复默认样式

### Requirement: Footer displays copyright
Footer 区域 SHALL 展示版权信息。

#### Scenario: Footer visible on main shell
- **WHEN** 主界面显示
- **THEN** Footer 区域可见并展示版权信息文字
