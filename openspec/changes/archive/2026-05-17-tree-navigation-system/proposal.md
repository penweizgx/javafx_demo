## Why

当前项目使用扁平的按钮导航，无法满足企业级应用的多层级导航需求。需要实现一个完整的树形导航系统，支持：
- 4级导航菜单（系统 > 用户管理 > 用户列表 > 基本信息Tab）
- 快捷入口（Header + Dashboard）
- 路由系统（路径导航、参数传递、跨Tab历史）
- 导航守卫（权限控制、登录检查、表单离开确认）

## What Changes

- **新增 NavigationPane 组件**：树形导航菜单，支持4级结构，多开模式，展开/折叠动画
- **新增 Router 系统**：路径导航、参数传递、跨Tab历史管理、子页面支持
- **新增导航守卫机制**：全局守卫、路由独享守卫、组件内守卫，支持同步/异步
- **新增 EventBus**：Router 与 NavigationPane 解耦通信
- **新增配置文件**：navigation.yaml 合并定义导航结构和路由
- **新增快捷入口**：QuickAccessBar（Header）+ Dashboard 卡片

## Capabilities

### New Capabilities

- `tree-navigation`: 树形导航菜单，支持4级结构、展开/折叠、选中状态同步
- `router-system`: 路由系统，支持路径导航、参数传递、跨Tab历史、子页面
- `navigation-guard`: 导航守卫，支持权限控制、登录检查、表单离开确认
- `quick-access`: 快捷入口，Header 固定入口 + Dashboard 卡片

### Modified Capabilities

- `shell-layout`: 侧边栏从扁平按钮改为 NavigationPane 组件

## Impact

- 新增文件：
  - `NavigationPane.java`, `NavGroup.java`, `NavItem.java`, `NavSubItem.java`
  - `Router.java`, `RouteParams.java`, `PageRegistry.java`, `HistoryManager.java`
  - `EventBus.java`, `RouteChangeEvent.java`, `NavigationClickEvent.java`
  - `NavigationGuard.java`, `GuardResult.java`, `AuthGuard.java`, `PermissionGuard.java`
  - `navigation.yaml`, `NavigationConfig.java`, `NavigationConfigLoader.java`
  - `QuickAccessBar.java`, `DashboardCard.java`
- 修改文件：
  - `ShellController.java`: 集成 NavigationPane、Router、QuickAccessBar
  - `ShellViewModel.java`: 添加导航相关属性
  - `pom.xml`: 添加 YAML 解析依赖
