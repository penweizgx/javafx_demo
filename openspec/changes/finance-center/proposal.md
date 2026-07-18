## Why

当前应用缺少财务中心模块，无法满足幼儿园管理场景中收费、退费、统计等核心财务业务需求。后端已提供完整的财务 API（30个端点），前端需要实现6个页面对接这些接口，覆盖学生账户管理、费用记录查询、考勤退费、项目标准维护、统计报表和系统配置。

## What Changes

- 新增"财务中心"导航模块，包含6个子页面：学生账户、费用记录、考勤退费、项目标准、统计报表、配置
- 新增 FinanceApiServiceImpl，封装全部财务模块 HTTP 请求
- 新增 FinanceManageService 接口及实现，编排财务业务逻辑
- 新增6个页面的 Controller/ViewModel/FXML
- 新增收费弹窗、设置收费标准弹窗、新增项目弹窗、退费设置弹窗、添加标准弹窗等组件
- 扩展 FinAccountVO 模型，对齐后端完整字段（feeScales、student、subjectExpireDate）
- 新增多个 VO/DTO/QO 模型对齐后端 API Schema
- 更新 navigation.yaml 添加财务中心导航节点
- 更新 AppModule 添加 Guice 绑定
- 更新 i18n 资源文件添加财务相关文本
- 将现有 StudentApiServiceImpl 中的 financeAccount() 迁移到 FinanceApiServiceImpl

## Capabilities

### New Capabilities
- `finance-account`: 学生账户页面 — 仪表盘简报、学生账户列表、查看详情弹窗、收费弹窗、设置收费标准弹窗、离校归档、导入收费数据、导出
- `finance-bills`: 费用记录页面 — 分页查询收费流水、查看详情、作废收费单/结转单、导出
- `finance-attend-refund`: 考勤退费页面 — 按班级/月份查退费结转、异常信息展示、确认结转、手动退费
- `finance-subject`: 项目标准页面 — 收费项目CRUD、展开子表查看收费标准、退费设置、添加/停用标准、批量收费
- `finance-reports`: 统计报表页面 — 四栏汇总表、按收费项分析、退费结转项目分析、支付方式统计
- `finance-config`: 配置页面 — 支付方式管理、票据号规则、提醒期、考勤退费阈值、核查账户

### Modified Capabilities

## Impact

- **新增文件**: 约 30+ Java 文件（Controller/ViewModel/Service/Model）、6+ FXML 文件、5+ 弹窗组件
- **修改文件**: `navigation.yaml`、`AppModule.java`、`ApiUrl.java`、`messages_zh_CN.properties`、`messages_en_US.properties`
- **迁移**: `StudentApiServiceImpl.financeAccount()` → `FinanceApiServiceImpl`
- **API 依赖**: 30个 `/finance/*` 端点，复用现有 `StudentQO`、`StudentVO` 等模型
- **图表**: 统计报表页需要图表渲染能力（JavaFX 内置 Charts 或引入第三方库）
