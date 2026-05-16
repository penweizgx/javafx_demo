## Context

当前项目导航是扁平的按钮列表，无法支持：
- 多层级菜单结构
- 页面参数传递
- 导航历史管理
- 权限控制

需要设计一个完整的导航系统，支持企业级应用的复杂导航需求。

## Goals / Non-Goals

**Goals:**
- 实现4级导航菜单（L1分组 > L2分组 > L3页面 > L4 Tab定位）
- 实现路由系统，支持路径导航、参数传递、跨Tab历史
- 实现导航守卫，支持权限控制、登录检查、表单离开确认
- 实现快捷入口，Header固定入口 + Dashboard卡片
- 配置化：navigation.yaml 统一定义导航结构和路由

**Non-Goals:**
- 不实现导航菜单的拖拽排序
- 不实现导航菜单的搜索功能
- 不实现侧边栏折叠功能

## Decisions

### 1. NavigationPane 实现：Accordion + VBox

**决策**: L1分组使用 VBox + 自定义展开/折叠，L2+使用 VBox + 自定义按钮。

**理由**:
- 样式灵活，不同层级可以有不同样式
- 展开/折叠动画自然
- 适合4级结构

### 2. Router 历史管理：跨Tab历史

**决策**: 历史栈记录所有Tab的打开顺序，back/forward 跨Tab切换。

**理由**:
- 符合用户直觉（类似浏览器）
- 支持复杂的多Tab场景

### 3. Router 与 NavigationPane 同步：EventBus

**决策**: 使用 EventBus 发布/订阅 RouteChangeEvent，解耦 Router 和 NavigationPane。

**理由**:
- 完全解耦
- 其他组件（BreadCrumb、TitleBar）也可以监听
- 易于扩展

### 4. 配置文件组织：合并配置

**决策**: navigation.yaml 同时定义导航结构和路由配置。

**理由**:
- 一个文件管理所有页面
- 减少配置冗余
- 易于维护

### 5. 导航守卫：同步+异步

**决策**: 支持同步守卫（本地状态检查）和异步守卫（API调用）。

**理由**:
- 同步守卫：登录状态、表单修改检查
- 异步守卫：权限验证、数据检查
- CompletableFuture 链式执行

### 6. 确认对话框：Router 处理

**决策**: GuardResult.confirm() 返回确认请求，Router 统一调用 DialogService。

**理由**:
- 守卫不需要依赖 DialogService
- 确认对话框样式统一

### 7. 权限配置：配置文件

**决策**: navigation.yaml 中每个页面定义所需权限。

**理由**:
- 配置与代码分离
- 易于修改权限规则

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Shell                                        │
├─────────────────────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │ Header: Brand | QuickAccessBar | SystemBtn | User             │  │
│  └───────────────────────────────────────────────────────────────┘  │
│  ┌────────────┐  ┌────────────────────────────────────────────────┐ │
│  │ Navigation │  │ BreadCrumb                                    │ │
│  │ Pane       │  ├────────────────────────────────────────────────┤ │
│  │            │  │                                                │ │
│  │  ▼ 系统    │  │ TabPane                                        │ │
│  │    用户    │  │ ┌──────────┬──────────┬──────────┐            │ │
│  │    角色    │  │ │ Tab      │ Tab      │ Tab      │            │ │
│  │  ▼ 业务    │  │ └──────────┴──────────┴──────────┘            │ │
│  │    订单    │  │                                                │ │
│  │            │  │ [Content Area]                                │ │
│  └────────────┘  └────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

组件关系:
navigation.yaml → NavigationConfig → NavigationPane + PageRegistry
NavItem.onClick → EventBus → Router.navigate → PageRegistry → TabPane
Router → EventBus → NavigationPane.selectByPath
```

## Risks / Trade-offs

### [Risk] 配置文件过大难以维护
→ **Mitigation**: 按模块拆分配置文件，使用 YAML anchor 复用

### [Risk] 异步守卫链执行复杂
→ **Mitigation**: 使用 CompletableFuture 链式调用，统一异常处理

### [Risk] 跨Tab历史与用户预期不符
→ **Mitigation**: 提供清空历史功能，支持历史列表查看

## Open Questions

无（设计方案已明确）