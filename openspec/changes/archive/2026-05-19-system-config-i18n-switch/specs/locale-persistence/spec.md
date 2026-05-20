# locale-persistence

语言偏好的持久化存储与启动恢复。

## 行为规范

### 持久化存储
- 使用 `java.util.prefs.Preferences.userNodeForPackage(I18nService.class)`
- 存储键名：`app.locale`
- 存储值：Locale 的 `toString()` 格式（如 "zh_CN"、"en_US"）
- 语言切换时立即写入，无需延迟或批量写入

### 启动恢复
- I18nService 初始化时读取 Preferences 中的 `app.locale`
- 如果存在有效值，设置为当前 Locale；否则使用系统默认 Locale
- 恢复时机：I18nService 构造函数或 `@Inject` 初始化阶段

### 异常处理
- Preferences 读取失败时静默降级到系统默认 Locale
- 存储的值无法解析为有效 Locale 时，降级到系统默认 Locale
- 不抛出异常，不影响应用启动