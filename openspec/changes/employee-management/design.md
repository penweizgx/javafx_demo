# Design: 员工管理功能

## 数据模型

### Employee（对应后端 EmployeeVO）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 员工ID |
| name | String | 姓名 |
| gender | String | 性别（男/女） |
| birthday | String | 出生日期 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| orgName | String | 所属机构 |
| hireDate | String | 入职日期 |
| status | Integer | 状态（1-在职 0-离职） |

### EmployeeListReq（请求参数）

| 字段 | 类型 | 说明 |
|------|------|------|
| pageNum | Integer | 页码，默认1 |
| pageSize | Integer | 每页条数，默认10 |
| name | String | 姓名模糊查询（可选） |
| orgName | String | 机构模糊查询（可选） |

## API 调用

- **接口**: POST `/api/employee/list`
- **请求体**: EmployeeListReq JSON
- **响应体**: `{ "code": 200, "data": { "records": [...], "total": 100, "pageNum": 1, "pageSize": 10 } }`
- **认证**: Header `Authorization: Bearer <token>`

## 类设计

### EmployeeManageService

```java
public interface EmployeeManageService {
    CompletableFuture<PageResult<Employee>> listEmployees(EmployeeListReq req);
}
```

### EmployeeManageServiceImpl

- 通过 ApiService 发送 POST 请求到 `/api/employee/list`
- 解析响应 JSON，转换为 `PageResult<Employee>`
- 使用 AsyncExecutor 执行异步请求

### EmployeeListViewModel

- 持有 ObservableList<Employee>、分页信息、搜索条件
- 提供 loadEmployees() 方法触发异步查询
- 继承 ViewModelBase

### EmployeeListController

- FXML 控制器，绑定 employee_list.fxml
- TableView 展示员工数据列：姓名、性别、手机号、邮箱、机构、入职日期、状态
- 顶部搜索栏：姓名输入框 + 搜索按钮
- 底部分页控件：上一页/下一页 + 页码显示
- 支持语言切换刷新表头

## FXML 布局

```
VBox
├── HBox (搜索栏)
│   ├── TextField (姓名搜索)
│   └── Button (搜索)
├── TableView (员工数据)
│   ├── TableColumn: 姓名
│   ├── TableColumn: 性别
│   ├── TableColumn: 手机号
│   ├── TableColumn: 邮箱
│   ├── TableColumn: 机构
│   ├── TableColumn: 入职日期
│   └── TableColumn: 状态
└── HBox (分页)
    ├── Button: 上一页
    ├── Label: 当前页/总页数
    └── Button: 下一页
```

## 导航变更

navigation.yaml 中将 `nav.user.list=用户列表` 改为 `nav.employee.list=员工列表`，路由 `/system/user/list` 改为 `/system/employee/list`，FXML 指向 `employee_list`。

## Guice 绑定

AppModule 中新增 `bind(EmployeeManageService.class).to(EmployeeManageServiceImpl.class);`
