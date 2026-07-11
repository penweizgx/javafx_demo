## Context

当前导航配置中：
- "员工管理" (id: employee-mgmt) 位于"系统"模块下，作为子菜单
- "角色管理" (id: role-mgmt) 位于"系统"模块下，作为子菜单

这种层级结构导致用户需要先点击"系统"，再点击对应功能，操作路径较长。

## Goals / Non-Goals

**Goals:**
- 将"员工管理"和"角色管理"提升为顶级菜单项
- 保持现有路由路径不变（/system/employee/list 和 /system/role/list）
- 保持所有子页面配置不变
- 最小化代码变更

**Non-Goals:**
- 不修改功能页面逻辑
- 不修改路由处理器
- 不修改国际化配置

## Decisions

**Approach:** 直接修改 navigation.yaml 的层级结构

- 将 employee-mgmt 和 role-mgmt 从 system.children 移动到 navigation 顶级
- 保留其所有子节点和配置
- 移除 system.children 中对这两项的引用

**Why this approach:**
- 最简单直接，只需修改配置文件
- 不影响现有代码逻辑
- 路由系统通过 path 匹配，不依赖菜单层级
- 可回滚（只需恢复 navigation.yaml）

## Risks / Trade-offs

- [No major risks] 本次变更仅影响UI展示层级，不影响任何业务逻辑

## Migration Plan

1. 备份 current navigation.yaml
2. 修改 navigation.yaml，调整菜单层级
3. 运行 `mvn compile` 验证配置加载正常
4. 运行 `mvn javafx:run` 验证UI显示正确
5. 提交更改并归档此变更
