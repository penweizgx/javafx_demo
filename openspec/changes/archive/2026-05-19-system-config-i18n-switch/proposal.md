## Why

系统配置页面当前缺少国际化语言切换功能。用户无法在运行时切换中/英文界面，且语言偏好不会持久化，每次重启应用都恢复为系统默认语言。需要在系统配置页面的第一项提供语言切换选项，并将选择保存到配置文件中。

## What Changes

- 新增系统配置页面（`system_config.fxml` + `SystemConfigController`），作为导航中"系统"模块的第一项
- 配置页面第一项为国际化语言切换（中文/英文），使用下拉选择器
- 语言切换后立即刷新当前界面所有文本（通过 EventBus 广播 `LocaleChangeEvent`）
- 语言偏好持久化到 `java.util.prefs.Preferences`，应用启动时读取并应用
- `I18nService` 增加语言持久化和启动恢复逻辑
- 在 `messages_zh_CN.properties` 和 `messages_en_US.properties` 中添加配置页面相关文本
- 在 `navigation.yaml` 中注册系统配置页面的导航路由

## Capabilities

### New Capabilities
- `system-config`: 系统配置页面，包含国际化语言切换作为第一配置项
- `locale-persistence`: 语言偏好的持久化存储与启动恢复

### Modified Capabilities
- `i18n-service`: I18nService 需支持持久化语言偏好和 EventBus 事件广播

## Impact

- 新增文件：`system_config.fxml`、`SystemConfigController.java`、`SystemConfigViewModel.java`
- 修改文件：`I18nService.java`（增加持久化和事件广播）、`navigation.yaml`（注册新路由）、`messages_zh_CN.properties` 和 `messages_en_US.properties`（添加新文本键）、`AppModule.java`（绑定新服务）、`ShellController.java`（响应语言切换刷新界面）
- 依赖：`java.util.prefs.Preferences`（已有使用）、`EventBus`（已有）