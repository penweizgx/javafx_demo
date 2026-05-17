## ADDED Requirements

### Requirement: Footer copyright text
Footer 区域 SHALL 展示版权信息文字。

#### Scenario: Footer shows copyright
- **WHEN** 主界面显示
- **THEN** Footer 区域展示版权信息 "© 2024 JavaFX MVVM Demo"

### Requirement: Footer styling
Footer 区域 SHALL 具有与主题适配的背景色和边框样式。

#### Scenario: Footer light theme styling
- **WHEN** 使用亮色主题
- **THEN** Footer 背景为浅灰色
- **AND** Footer 顶部有细边框分隔线

#### Scenario: Footer dark theme styling
- **WHEN** 使用暗色主题
- **THEN** Footer 背景为深色
- **AND** Footer 顶部有边框分隔线

### Requirement: Footer fixed height
Footer 区域 SHALL 具有固定高度，不随内容变化。

#### Scenario: Footer height consistent
- **WHEN** 主界面显示
- **THEN** Footer 高度约为 32px
- **AND** Footer 高度不随窗口大小变化而改变
