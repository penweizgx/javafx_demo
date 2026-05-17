## Why

当前用户列表页面点击"查看"按钮会跳转到独立的详情页面，但对于快速查看员工基本信息的需求，弹窗形式更加便捷高效，用户无需离开列表页面即可查看详情，提升用户体验。

## What Changes

- 在用户列表页面添加弹窗形式的员工详情查看功能
- 点击"查看"按钮时，以模态弹窗展示员工详细信息
- 弹窗包含员工的基本信息（姓名、邮箱、电话、部门、角色、状态等）
- 使用AtlantaFX的ModalPane组件实现弹窗
- 保留原有的详情页面跳转功能（双击行或从导航进入）

## Capabilities

### New Capabilities
- `employee-detail-modal`: 员工详情弹窗组件，支持快速查看员工信息

### Modified Capabilities
- 无现有specs需要修改

## Impact

- **UserListController**: 添加弹窗显示逻辑
- **新增组件**: EmployeeDetailModal 弹窗组件
- **依赖**: 使用现有的 DialogService 或 AtlantaFX ModalPane
