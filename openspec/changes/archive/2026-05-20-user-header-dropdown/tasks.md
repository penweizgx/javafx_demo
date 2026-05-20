## 1. Shell 顶部用户区域 UI 改造

- [x] 1.1 修改 shell.fxml，在 userNameLabel 左侧添加头像占位（FontIcon person 图标 + 圆形裁剪容器）
- [x] 1.2 修改 ShellController，将 userNameLabel 点击事件从弹出 Popover 改为显示 ContextMenu 下拉菜单
- [x] 1.3 创建 ContextMenu，包含"查看登录信息"和"退出登录"两个 MenuItem

## 2. 查看登录信息功能

- [x] 2.1 保留现有 showUserDetails() 逻辑，改为由"查看登录信息"菜单项触发
- [x] 2.2 Popover 内容文本支持 i18n

## 3. 退出登录功能

- [x] 3.1 在 ShellController 中实现 logout() 方法：调用 TokenStorage.clear() 清理 Token
- [x] 3.2 在 MainApp 中暴露 showLogin() 静态方法，用于重新显示登录窗口
- [x] 3.3 logout() 中关闭当前 Stage 并调用 MainApp.showLogin() 返回登录页
- [x] 3.4 退出前清理 ShellController 的 EventBus 订阅

## 4. i18n 文本

- [x] 4.1 在 messages_zh_CN.properties 中添加 shell.user.viewInfo、shell.user.logout、shell.user.close 键
- [x] 4.2 在 messages_en_US.properties 中添加对应英文键
- [x] 4.3 ShellController 的 refreshI18n() 方法中添加菜单项文本刷新

## 5. 编译验证

- [x] 5.1 运行 mvn compile 验证编译通过
