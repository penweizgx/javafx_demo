## Context

当前应用已实现学生管理、员工管理等模块，采用 MVVM 架构（Controller → ViewModel → Service → ApiService），通过 AppContext/Guice 手动依赖注入，Router 实现基于 Tab 的页面导航。财务相关代码仅存在于学生详情页的一个余额展示卡片，`FinAccountVO` 模型只有5个字段，`StudentApiServiceImpl` 中有一个 `financeAccount()` 方法。

后端已提供完整的财务 API（30个端点），覆盖学生账户、费用记录、考勤退费、项目标准、统计报表、配置6大功能域。需要新建独立的财务中心模块，遵循现有架构模式。

## Goals / Non-Goals

**Goals:**
- 实现6个财务页面，完整对接后端30个API端点
- 遵循现有 MVVM 架构模式，保持代码风格一致
- 将财务 API 调用从 StudentApiServiceImpl 迁移到独立的 FinanceApiServiceImpl
- 扩展 FinAccountVO 对齐后端完整数据结构
- 收费弹窗支持3类收费项选择、余额抵扣、多种支付方式
- 统计报表页支持图表展示

**Non-Goals:**
- 打印票据功能（本次不实现）
- 收费数据导入的文件上传交互（仅预留按钮，不实现拖拽上传）
- 离校退费的独立统计端点（已包含在 countChargeReport 中）
- 修改后端 API

## Decisions

### D1: 财务 API 服务独立为 FinanceApiServiceImpl

**选择**: 新建 `FinanceApiServiceImpl extends OkHttpApiServiceImpl`，将 `StudentApiServiceImpl.financeAccount()` 迁移过来，所有 `/finance/*` 端点统一收拢。

**理由**: 遵循项目已有的模块化 ApiService 模式（AuthApiServiceImpl、EmployeeApiServiceImpl、StudentApiServiceImpl 各自独立）。财务模块有30个端点，放在 StudentApiServiceImpl 中会导致职责膨胀。

**替代方案**: 继续在 StudentApiServiceImpl 中添加财务方法 → 拒绝，违反单一职责，StudentApiServiceImpl 已有170行。

### D2: 收费弹窗设计为独立组件 ChargeModal

**选择**: 收费弹窗作为独立组件类 `ChargeModal`，内部调用 `FinanceManageService.buildChargeForm()` 获取表单数据，用户填写后调用 `charge()` 提交。

**理由**: 收费弹窗是最复杂的交互（3类收费项选择 + 时段填写 + 余额抵扣 + 多支付方式），独立组件便于维护和复用。参考现有 `StudentCreateModal` 模式。

**数据流**:
1. `GET /finance/buildChargeForm/{studentId}` → `ChargeFormBuildDTO`（收费项+支付方式+缴费人+余额）
2. 用户选择收费项、填写金额/时段、选择支付方式
3. `POST /finance/charge` → `ChargeBillDTO`（提交收费单）

### D3: 设置收费标准弹窗按 Subject 分 Tab 展示

**选择**: 弹窗内使用 `TabPane`，每个收费项目（Subject）一个 Tab，Tab 内展示该项目下的 `FeeScale` 列表作为复选框。用户勾选后收集所有 feeScale.id，调用 `POST /finance/feeScale/bind`。

**理由**: 功能清单明确要求按 Tab 分类展示，后端 `GET /finance/subject/list` 返回的 `SubjectWithFeeScaleDTO` 已包含分组数据，前端按 subject 分组渲染即可。

### D4: 统计报表图表使用 JavaFX 内置 Charts

**选择**: 使用 `javafx.scene.chart.PieChart` 和 `javafx.scene.chart.BarChart`，不引入第三方依赖。

**理由**: 项目未引入任何图表库，JavaFX 内置 Charts 足以满足饼图（支付方式占比、退费项目占比）和柱状图（收费项分析）需求。避免引入新依赖增加复杂度。

**替代方案**: 引入 FlexGanttFX 或其他第三方库 → 拒绝，过度设计。

### D5: FinAccountVO 扩展而非新建模型

**选择**: 扩展现有 `FinAccountVO`，添加 `feeScales`、`student`、`subjectExpireDate`、`id`、`version`、`createdAt`、`modifiedAt` 字段，对齐后端完整 Schema。

**理由**: 后端 `/finance/account/{studentId}` 和 `/finance/listByCondition` 返回同一个 `FinAccountVO` 结构，扩展比新建更简洁，且学生详情页已有的余额展示代码无需修改。

### D6: 考勤退费页异常信息直接展示后端数据

**选择**: 退费列表中"异常信息"列直接展示后端 `MonthAttendCarryOverVO` 返回的异常字段，不做前端计算。

**理由**: 异常由后端退费定时任务在计算过程中标记，前端只需展示。

### D7: PeriodUnit 枚举前端映射

**选择**: 新建 `PeriodUnit` 枚举类，包含 `ONCE/M/Q/T/HY/FY/Y`，提供 `getLabel()` 和 `getMonthCount()` 方法。`FY` 和 `Y` 统一映射为"年"。

**映射表**:
| 枚举 | 月数 | 中文 |
|------|------|------|
| ONCE | 0 | 次 |
| M | 1 | 月 |
| Q | 3 | 季度 |
| T | 5 | 学期 |
| HY | 6 | 半年 |
| FY | 12 | 年 |
| Y | 12 | 年 |

### D8: 费用记录页使用 POST 分页查询

**选择**: 费用记录列表调用 `POST /finance/bills/list`，请求体为 `FilterBillsPage`，支持分页+多条件筛选。

**理由**: 后端设计为 POST 接口，支持复杂筛选条件（searchWord/clazzId/billType/period/filterInvalid），返回 `PaginationBillTimeDTO` 含分页元数据。

### D9: 导航结构

**选择**: 在 navigation.yaml 中新增 `finance-mgmt` 顶级节点，包含6个子节点。学生账户页为默认落地页。

```
finance-mgmt (财务中心)
├── finance-account (学生账户) /finance/account
├── finance-bills (费用记录) /finance/bills
├── finance-attend (考勤退费) /finance/attend
├── finance-subject (项目标准) /finance/subject
├── finance-reports (统计报表) /finance/reports
└── finance-config (配置) /finance/config
```

## Risks / Trade-offs

- **[countChargeReport 返回 Map<String,Object>]** → 已通过实际调用确认结构为 `{charge, balances, carryOver, refund}` 四个对象，各含 billnumber/studentNum/amount 等字段。前端用 `Map<String, ChargeReportItem>` 接收，避免硬编码。
- **[MonthAttendCarryOverVO 异常字段名不确定]** → 实现时调用 API 验证具体字段名，可能需要微调模型。
- **[收费弹窗复杂度高]** → 拆分为 ChargeModal 独立组件，内部按步骤组织（选项目→填金额→选支付方式），降低单文件复杂度。
- **[FinAccountVO 扩展可能影响学生详情页]** → 新增字段不影响现有代码（Lombok @Data 自动生成 getter），学生详情页只读取 balancesAmount，其他字段为 null 时不展示。
