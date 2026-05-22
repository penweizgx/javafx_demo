# Proposal: 员工管理功能

## 问题

当前"用户管理->用户列表"页面使用MockUserManageService提供模拟数据，没有对接真实后端API。需要将其改造为"员工管理"功能，对接后端 `/api/employee/list` 接口，展示真实的员工数据。

## 方案

将现有的用户列表页面改造为员工管理页面：

1. 新建 Employee 模型，字段对齐后端 EmployeeVO 结构（id、姓名、性别、出生日期、手机号、邮箱、机构、入职日期、状态等）
2. 新建 EmployeeManageService 接口及实现，调用 `/api/employee/list` 接口，支持分页查询
3. 将 UserListController/UserListViewModel 改造为 EmployeeListController/EmployeeListViewModel，展示员工数据表格
4. 更新导航配置，将"用户列表"改为"员工列表"，路由指向新的员工列表页面
5. 更新 i18n 资源，添加员工管理相关文本

## 范围

- 新增：Employee 模型、EmployeeManageService 接口与实现、EmployeeListController、EmployeeListViewModel、employee_list.fxml
- 修改：navigation.yaml（导航节点重命名+路由指向）、i18n 资源文件
- 保留：原有 User/UserManageService 不删除（其他模块可能引用），但导航中不再展示用户列表
