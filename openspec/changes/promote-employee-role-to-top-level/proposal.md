## Why

当前导航菜单的"员工管理"和"角色管理"位于较深的层级，用户需要多次点击才能访问。将这两个常用功能提升到顶级菜单，可以提升用户操作效率和体验。

## What Changes

- 将"员工管理"节点从当前父级菜单下移出，提升为顶级菜单项
- 将"角色管理"节点从当前父级菜单下移出，提升为顶级菜单项
- 调整导航配置文件 `navigation.yaml` 中的层级结构
- 相关的路由和菜单渲染逻辑保持不变

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- navigation: 导航菜单的层级结构和显示顺序将被修改

## Impact

**Affected files:**
- `navigation.yaml` - 导航配置文件，需要调整菜单层级

**Affected systems:**
- `NavigationConfig` / `NavigationPane` - 导航菜单的加载和渲染
- `Router` - 路由注册和匹配（仅配置变更，无逻辑变更）
