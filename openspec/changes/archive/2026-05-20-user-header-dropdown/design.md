# 用户头部下拉菜单 - 技术设计

## 背景

当前 ShellController 的用户区域仅显示 `userNameLabel`（Label），点击后弹出 Popover 显示用户详情。需要改造为"头像 + 用户名 + 下拉菜单"模式，并增加退出登录功能。

现有相关代码：
- `ShellController`：持有 `userNameLabel`、`viewModel`（ShellViewModel）、`showUserDetails()` 方法
- `ShellViewModel`：持有 `User` 数据，提供 `getUserName()`、`getCurrentUserValue()`
- `TokenStorage`：Token 持久化，需调用 `clear()` 清理
- `MainApp`：应用入口，控制 Stage 生命周期

## 目标 / 非目标

**目标：**
- 用户区域显示头像占位 + 用户名
- 下拉菜单提供"查看登录信息"和"退出登录"
- 退出登录清理 Token 并返回登录窗口
- 菜单文本支持 i18n

**非目标：**
- 不实现真实头像上传/显示
- 不实现多账户切换
- 不修改 ShellViewModel 的数据获取逻辑

## 决策

### 1. 下拉菜单组件选择：AtlantaFX DropdownMenu vs JavaFX ContextMenu

**选择：ContextMenu** — JavaFX 原生 ContextMenu 轻轻量，与 AtlantaFX 样式兼容好，无需额外依赖。DropdownMenu 是 AtlantaFX 的控件但需要更多配置，ContextMenu 在此场景更简洁。

### 2. 退出登录实现方式

**选择：通过 Stage 引用关闭当前窗口，由 MainApp 重新显示登录窗口**

流程：
1. `TokenStorage.clear()` 清理 Token
2. 获取当前 Stage（从 `userNameLabel.getScene().getWindow()`）
3. 关闭当前 Stage
4. `Platform.runLater` 中调用 `MainApp.showLogin()` 重新显示登录窗口

需要在 `MainApp` 中暴露 `showLogin()` 静态方法，或通过 `AppContext` 传递登录回调。

### 3. 头像占位实现

**选择：使用 FontIcon（Ikonli）+ 圆形裁剪** — 用 Material2 的 `person` 图标作为默认头像，配合 CSS 圆形样式。无需图片资源。

## 风险 / 权衡

- **Stage 生命周期**：关闭主 Stage 后重新打开登录窗口需要确保所有资源释放干净 → 在退出前清理 Router、EventBus 订阅
- **i18n 键缺失**：需确保 `shell.user.viewInfo` 和 `shell.user.logout` 在两个 properties 文件中都存在
