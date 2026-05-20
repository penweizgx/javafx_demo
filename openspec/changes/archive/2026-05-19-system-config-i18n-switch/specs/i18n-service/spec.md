# i18n-service

I18nService 的修改，增加持久化和事件广播能力。

## 行为规范

### 新增方法

#### `saveLocalePreference(Locale locale)`
- 将语言偏好保存到 Preferences
- 调用时机：`setLocale()` 成功切换语言后自动调用

#### `loadLocalePreference(): Optional<Locale>`
- 从 Preferences 读取语言偏好
- 返回 `Optional.empty()` 表示无已保存偏好或读取失败
- 调用时机：I18nService 初始化时

### 修改方法

#### `setLocale(Locale locale)`
- 切换语言后，自动调用 `saveLocalePreference(locale)`
- 切换语言后，通过 EventBus 发布 `LocaleChangeEvent`
- 保持现有行为不变（更新 ResourceBundle 等）

### 新增事件

#### `LocaleChangeEvent`
- 字段：`Locale newLocale`
- 发布时机：`setLocale()` 成功切换后
- 订阅方：所有需要刷新文本的 Controller

### 初始化流程
1. 构造时调用 `loadLocalePreference()`
2. 如果有已保存偏好，设置为当前 Locale
3. 否则使用系统默认 Locale