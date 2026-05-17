## Why

当前导航菜单结构不合理，将页面内部操作元素（如"用户详情"下的"基本信息"、"操作记录"等）显示在主导航栏中，导致用户无法通过导航栏直接访问这些功能，且混淆了导航层级概念。这些子元素应该是页面内部的Tab切换或子页面导航，而非全局导航项。

## What Changes

- 重新设计导航菜单层级结构，明确区分全局导航和页面内部导航
- 移除导航栏中不应显示的页面内部操作元素
- 将"用户详情"下的Tab项（基本信息、操作记录、权限设置）从导航配置中移除
- 调整navigation.yaml配置，优化菜单分组和层级关系
- 更新NavGroup和NavItem组件，支持区分"可导航项"和"页面内Tab项"
- 简化导航菜单，只保留可直接访问的功能页面

## Capabilities

### New Capabilities
- `navigation-hierarchy`: 导航层级规范，定义哪些节点应该在导航栏显示，哪些是页面内部元素

### Modified Capabilities
- 无现有specs需要修改

## Impact

- **navigation.yaml**: 需要重新组织菜单结构
- **NavigationNode**: 可能需要添加属性区分导航类型
- **NavGroup/NavItem**: 需要过滤掉不应显示的节点
- **用户详情页**: 需要在页面内部实现Tab切换，而非依赖导航栏