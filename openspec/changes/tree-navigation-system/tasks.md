## 1. 基础设施

- [ ] 1.1 创建 `EventBus.java` - 事件总线
- [ ] 1.2 创建 `RouteChangeEvent.java` - 路由变化事件
- [ ] 1.3 创建 `NavigationClickEvent.java` - 导航点击事件
- [ ] 1.4 创建 `TabClosedEvent.java` - Tab关闭事件

## 2. 导航数据模型

- [ ] 2.1 创建 `NavigationNode.java` - 导航节点数据类
- [ ] 2.2 创建 `NavigationConfig.java` - 导航配置类
- [ ] 2.3 创建 `navigation.yaml` - 导航配置文件
- [ ] 2.4 创建 `NavigationConfigLoader.java` - YAML加载器

## 3. NavigationPane 组件

- [ ] 3.1 创建 `NavSubItem.java` - L4子节点组件
- [ ] 3.2 创建 `NavSubItems.java` - L4容器组件
- [ ] 3.3 创建 `NavItem.java` - L3叶子节点组件（含展开L4逻辑）
- [ ] 3.4 创建 `NavGroup.java` - 可折叠分组组件（含动画）
- [ ] 3.5 创建 `NavigationPane.java` - 主容器组件
- [ ] 3.6 创建 `navigation.css` - 导航样式文件

## 4. Router 系统

- [ ] 4.1 创建 `RouteParams.java` - 路由参数类
- [ ] 4.2 创建 `RouteDefinition.java` - 路由定义
- [ ] 4.3 创建 `RouteMatch.java` - 路由匹配结果
- [ ] 4.4 创建 `PageOptions.java` - 页面选项
- [ ] 4.5 创建 `PageRegistry.java` - 页面注册表
- [ ] 4.6 创建 `RouteHistory.java` - 历史记录
- [ ] 4.7 创建 `HistoryManager.java` - 历史管理器
- [ ] 4.8 创建 `Router.java` - 路由器

## 5. 导航守卫

- [ ] 5.1 创建 `GuardResult.java` - 守卫结果类
- [ ] 5.2 创建 `NavigationGuard.java` - 全局守卫接口
- [ ] 5.3 创建 `RouteGuard.java` - 路由独享守卫接口
- [ ] 5.4 创建 `ComponentGuard.java` - 组件内守卫接口
- [ ] 5.5 创建 `AsyncNavigationGuard.java` - 异步守卫接口
- [ ] 5.6 创建 `AuthGuard.java` - 登录状态守卫
- [ ] 5.7 创建 `PermissionGuard.java` - 权限守卫
- [ ] 5.8 创建 `AuditLogGuard.java` - 审计日志守卫

## 6. Controller 接口

- [ ] 6.1 创建 `ParamReceiver.java` - 参数接收接口
- [ ] 6.2 创建 `SubPageContainer.java` - 子页面容器接口
- [ ] 6.3 创建 `PageLifecycle.java` - 页面生命周期接口

## 7. 快捷入口

- [ ] 7.1 创建 `QuickAccessItem.java` - 快捷入口数据类
- [ ] 7.2 创建 `QuickAccessBar.java` - Header快捷入口组件
- [ ] 7.3 创建 `DashboardCard.java` - Dashboard卡片组件

## 8. 集成

- [ ] 8.1 更新 `ShellController.java` - 集成 NavigationPane、Router
- [ ] 8.2 更新 `shell.fxml` - 替换扁平导航为 NavigationPane
- [ ] 8.3 更新 `AppModule.java` - 注册 Router、HistoryManager
- [ ] 8.4 更新 `pom.xml` - 添加 YAML 解析依赖 (snakeyaml)

## 9. 测试

- [ ] 9.1 创建 `NavigationConfigLoaderTest.java`
- [ ] 9.2 创建 `RouterTest.java`
- [ ] 9.3 创建 `HistoryManagerTest.java`
- [ ] 9.4 创建 `EventBusTest.java`
- [ ] 9.5 创建 `AuthGuardTest.java`
- [ ] 9.6 创建 `PermissionGuardTest.java`

## 10. 验证

- [ ] 10.1 运行 `mvn compile` 确保编译通过
- [ ] 10.2 运行 `mvn javafx:run` 验证导航功能
- [ ] 10.3 验证导航树展开/折叠动画
- [ ] 10.4 验证路由跳转和参数传递
- [ ] 10.5 验证后退/前进历史
- [ ] 10.6 验证权限守卫拦截