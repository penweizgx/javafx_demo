## 1. 资源准备

- [x] 1.1 创建 `src/main/resources/images/` 目录
- [x] 1.2 添加品牌图标文件 `logo.png`（占位图或用户提供）

## 2. FXML 布局重构

- [x] 2.1 重构 `shell.fxml`：添加 header 三区域布局（brand-area / spacer / system-area）
- [x] 2.2 重构 `shell.fxml`：添加 aside 侧边栏区域（VBox，prefWidth=240）
- [x] 2.3 重构 `shell.fxml`：将 TabPane 移入 content 区域
- [x] 2.4 重构 `shell.fxml`：添加 footer 底部区域（HBox）
- [x] 2.5 重构 `shell.fxml`：移动导航按钮（首页/表单/列表）到 aside 区域

## 3. Controller 逻辑调整

- [x] 3.1 移除 `btnBack` 字段及相关逻辑
- [x] 3.2 新增 `brandIcon`（ImageView）、`brandLabel`（Label）字段
- [x] 3.3 新增 `btnSystem`（Button）、`copyrightLabel`（Label）字段
- [x] 3.4 更新 `initialize()` 方法：设置品牌文字、版权文字
- [x] 3.5 更新 `initialize()` 方法：btnSystem 空实现（占位）
- [x] 3.6 更新菜单按钮样式逻辑：适配 aside 区域的 CSS 选择器

## 4. 样式文件更新

- [x] 4.1 更新 `base.css`：新增 `.header-bar`、`.brand-area`、`.brand-title`、`.system-area` 样式
- [x] 4.2 更新 `base.css`：新增 `.aside-nav`、`.nav-item`、`.nav-item.menu-selected` 样式
- [x] 4.3 更新 `base.css`：新增 `.footer-bar` 样式
- [x] 4.4 更新 `light.css`：亮色主题各区域颜色适配
- [x] 4.5 更新 `dark.css`：暗色主题各区域颜色适配

## 5. 国际化文案

- [x] 5.1 更新 `messages_zh_CN.properties`：新增 `shell.header.brand`、`shell.header.system`、`shell.footer.copyright`
- [x] 5.2 更新 `messages_en_US.properties`：新增对应英文文案

## 6. 验证

- [x] 6.1 运行 `mvn compile` 确保编译通过
- [x] 6.2 运行 `mvn javafx:run` 验证布局效果
- [x] 6.3 验证亮色/暗色主题切换正常
- [x] 6.4 验证导航按钮点击和选中状态正常
