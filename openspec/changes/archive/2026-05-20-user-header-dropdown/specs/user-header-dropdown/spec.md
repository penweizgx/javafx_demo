# 用户头部下拉菜单 - 能力规格

## user-header-dropdown

### 功能描述

Shell 顶部右侧用户区域改造：用户名 + 头像占位 + 下拉菜单。

### 交互规格

- 用户区域由"用户名文本"改为"头像占位 + 用户名文本"的水平布局
- 头像为圆形占位图，使用 AtlantaFX 样式
- 点击用户区域弹出下拉菜单（DropdownMenu / ContextMenu）
- 菜单项：
  1. "查看登录信息" — 弹出 Popover 或 Dialog 显示当前用户姓名、机构等
  2. "退出登录" — 清理 Token（TokenStorage）、关闭主 Shell、导航到登录窗口

### 数据依赖

- `ShellViewModel` 提供用户名和用户信息
- `TokenStorage` 提供 Token 清理能力
- `I18nService` 提供菜单项文本国际化

### 退出登录流程

1. 调用 `TokenStorage.clear()` 清理本地 Token
2. 关闭当前 Stage（主界面）
3. 由 `MainApp` 重新显示登录窗口

### i18n 键

- `shell.user.viewInfo` — 查看登录信息
- `shell.user.logout` — 退出登录
