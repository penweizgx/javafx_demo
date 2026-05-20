## Context

当前项目使用 `I18nService` 管理国际化，支持中文（zh_CN）和英文（en_US）两种语言。`I18nService` 已有 `setLocale()` 和 `toggleLanguage()` 方法，但：
1. 语言切换后不会通知其他 UI 组件刷新文本
2. 语言偏好不会持久化，每次启动恢复为系统默认 Locale
3. 没有系统配置页面供用户操作语言切换

项目已有 `EventBus` 事件机制、`java.util.prefs.Preferences` 持久化（TokenStorage 在用）、`ThemeService` 主题切换（可作为参考模式）。

## Goals / Non-Goals

**Goals:**
- 提供系统配置页面，第一项为语言切换
- 语言切换后立即刷新界面所有 i18n 文本
- 语言偏好持久化到 Preferences，应用启动时恢复
- 遵循现有 MVVM + Guice DI 架构

**Non-Goals:**
- 不支持两种语言之外的其他语言（后续可扩展）
- 不实现配置页面的其他配置项（如主题、代理等），仅预留位置
- 不修改 FXML 中硬编码的文本（仅处理通过 I18nService 获取的文本）

## Decisions

### 1. 语言持久化使用 Preferences 而非配置文件

**选择**: `java.util.prefs.Preferences`
**替代方案**: 自定义 properties/yaml 配置文件
**理由**: 项目 TokenStorage 已使用 Preferences，保持一致性；Preferences 是 Java 标准机制，无需额外依赖；适合存储简单的键值对（语言偏好）。

### 2. 语言切换通知使用 EventBus

**选择**: 通过 EventBus 发布 `LocaleChangeEvent`，各 Controller 订阅并刷新
**替代方案**: JavaFX 属性绑定（ObjectProperty<Locale>）
**理由**: 项目已有 EventBus 模式（RouteChangeEvent、NavigationClickEvent），保持架构一致性；EventBus 解耦更彻底，不需要每个 Controller 持有 I18nService 引用来绑定属性。

### 3. 系统配置页面作为导航路由页面

**选择**: 在 navigation.yaml 的"系统"模块下注册 `system-config` 节点，路由 `/system/config`
**替代方案**: 使用 Popover 弹窗（类似用户详情弹窗）
**理由**: 配置页面未来会扩展更多配置项，独立页面更合适；符合现有路由导航模式；用户期望点击"系统"后看到配置页面。

### 4. 界面刷新策略

**选择**: 各 Controller 监听 `LocaleChangeEvent` 后调用 `refreshI18n()` 方法重新设置文本
**替代方案**: 重新加载整个 FXML
**理由**: 重新加载 FXML 会丢失当前页面状态（表单数据、滚动位置等）；手动刷新文本更精确可控；这是 JavaFX 国际化的常见做法。

## Risks / Trade-offs

- **[部分文本未通过 I18nService 获取]** → 需要排查所有 Controller 中的硬编码中文文本，统一改为 I18nService 获取
- **[EventBus 内存泄漏]** → Controller 销毁时需要取消订阅，否则会持有引用导致 GC 无法回收。需在 Controller 中添加清理逻辑
- **[Preferences 跨平台差异]** → Preferences 在不同 OS 上存储位置不同，但行为一致，不影响功能