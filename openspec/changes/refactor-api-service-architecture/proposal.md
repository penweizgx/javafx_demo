## Why

当前业务API调用分散在不同实现类中：login()直接写在OkHttpApiServiceImpl中，EmployeeManageServiceImpl独立注入ApiService后手动拼装URL和解析JSON，未复用BaseApiServiceImpl已有的extractResBody、get/postJSON、execute（含重试）等能力。这导致URL硬编码风险、JSON解析逻辑重复、且每个业务模块都要自行处理token/重试/异常。需要将业务API调用统一收拢到继承OkHttpApiServiceImpl的模块化ApiServiceImpl中，复用父类基础设施。

## What Changes

- **新建 AuthApiServiceImpl**：继承OkHttpApiServiceImpl，将login()方法从OkHttpApiServiceImpl移入，复用父类的initRSAKey、extractResBody、configStorage等能力
- **新建 EmployeeApiServiceImpl**：继承OkHttpApiServiceImpl，将listEmployees的HTTP请求和JSON解析逻辑从EmployeeManageServiceImpl移入，复用父类的get/extractResBody
- **重构 OkHttpApiServiceImpl**：移除login()业务方法，仅保留HTTP基础设施（initHttp、拦截器、HttpClient构建），成为纯粹的HTTP底层服务
- **重构 AuthServiceImpl**：调用AuthApiServiceImpl.login()而非ApiService.login()
- **重构 EmployeeManageServiceImpl**：调用EmployeeApiServiceImpl获取数据，仅负责业务逻辑编排（异步执行、模型转换）
- **重构 EmployeeListReq**：调整字段以匹配EmployeeSearch（name, phone, active, orgNodeId）
- **更新 AppModule**：绑定新增的ApiServiceImpl类

## Capabilities

### New Capabilities
- `auth-api-service`: 认证模块的ApiServiceImpl，封装login的HTTP请求与响应解析
- `employee-api-service`: 员工模块的ApiServiceImpl，封装员工列表的HTTP请求与响应解析

### Modified Capabilities

## Impact

- OkHttpApiServiceImpl：移除login()方法，变为纯HTTP基础设施
- AuthServiceImpl：改为注入AuthApiServiceImpl
- EmployeeManageServiceImpl：改为注入EmployeeApiServiceImpl，移除直接HTTP调用逻辑
- AppModule：新增AuthApiServiceImpl和EmployeeApiServiceImpl的Guice绑定
- ApiService接口：login()方法保留（由AuthApiServiceImpl实现），getCurrentUser()保留（后续可移到UserApiServiceImpl）