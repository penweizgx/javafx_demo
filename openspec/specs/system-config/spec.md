# system-config

系统配置页面，提供应用级别的配置管理。

## 行为规范

### 页面布局
- 页面标题：通过 I18nService 获取 `system.config.title`
- 使用 AtlantaFX Card 组件作为配置项容器
- 每个配置项为水平布局：左侧标签 + 右侧控件

### 语言切换配置项（第一项）
- 标签文本：`I18nService.getString("system.config.language")`
- 控件：`ComboBox<Locale>` 下拉选择器
- 可选项：中文（zh_CN）、英文（en_US）
- 默认值：当前 I18nService 的 Locale
- 选择后立即触发语言切换，无需确认按钮
- 切换后当前页面文本立即刷新

### 路由
- 路由路径：`/system/config`
- 导航配置：在 navigation.yaml 的"系统"模块下注册
- 需要 AuthGuard 认证（非公开路由）

### MVVM 结构
- `SystemConfigController`：管理 FXML 控件引用，监听 LocaleChangeEvent 刷新文本
- `SystemConfigViewModel`：持有语言选择状态，调用 I18nService 切换语言
- ViewModel 通过 Controller 的 setViewModel() 注入（非 Guice 注入，遵循现有模式）

### 界面刷新
- Controller 订阅 EventBus 的 `LocaleChangeEvent`
- 收到事件后调用 `refreshI18n()` 方法重新设置所有文本
- Controller 销毁时取消 EventBus 订阅