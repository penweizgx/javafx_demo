## Context

现有应用是一个 JavaFX MVVM 桌面应用，使用 Guice 依赖注入、OkHttp API 客户端、AtlantaFX 主题组件库。导航通过 `navigation.yaml` 定义树形结构，经 `NavigationPane` + `Router` 实现页面路由和 Tab 管理。

缺少学生管理模块，现有 `Student.java` 为占位模型（仅 5 个字段），不满足业务需求。API 文档提供完整的学生管理端点。

## Goals / Non-Goals

**Goals:**
- 导航重构：删除"业务"示例模块，精简"系统"为仅系统配置，其余模块提升至顶层
- 新增学生管理模块，核心页面：班级卡片概览 → 班级学生列表 → 学生详情
- 全局学生搜索功能
- 学生详情页内原地编辑（不含调班）
- 弹窗新增学生
- 所有新增功能支持 i18n 中英文切换

**Non-Goals:**
- 班级的 CRUD 管理（属于组织机构管理，非本模块范围）
- 学生调班独立操作（后续独立功能）
- 学生毕业/退校操作（后续独立功能）
- Excel 导入学生（后续独立功能）
- 考勤签到/请假操作（仅展示考勤数据，不涉及操作）

## Decisions

### 1. 导航结构：学生管理作为顶层节点，点击即入班级卡片页

`student-mgmt` 节点同时拥有 `path`（点击时导航）和 `children`（子路由），Router 的 `registerNodeRoutes` 支持此模式。子页面全部 `showInNav: false`，仅通过页面内交互进入。

### 2. 全局搜索与班级卡片同页

搜索框放在班级卡片页顶部。输入关键词后，卡片视图切换为学生列表视图；清除搜索后恢复卡片视图。避免了单独的搜索页，减少路由跳转。

状态管理：ViewModel 持有一个 `searchText` 属性，非空时显示搜索结果列表，空时显示班级卡片。

### 3. 详情页：分组区块布局（非 Tab 页）

与 `user-detail` 的 `supportsSubPage` Tab 模式不同，学生详情使用单个 FXML 内的多个可视化区块（VBox/Section），每个区块展示一类信息。原因：
- 信息量适中，无需 Tab 切换
- 原地编辑时所有区块可同时编辑
- 实现更简单，无需 sub-page 路由

### 4. 编辑模式：ViewModel 驱动状态切换

ViewModel 持有 `editing: BooleanProperty`，FXML 中每个字段同时存在 Label（展示态）和 TextField/ComboBox（编辑态），通过 `visible` 属性绑定 `editing` 切换可见性。

编辑时从 `StudentVO` 拷贝数据到 `StudentFO` 编辑副本；保存调用 `POST /student/change`（不含 clazzId）；取消时还原原始值。

### 5. 弹窗新增：DialogService + ModalPane（复用现有组件）

不创建 FXML，直接构建 Java 组件（类似 `EmployeeDetailModal` 模式），通过 `DialogService.showModal()` 展示。表单字段使用 AtlantaFX 控件。

提交后通过回调刷新列表，不涉及路由跳转。

### 6. 模型设计：StudentVO 通吃

一个 `StudentVO` 模型对应 API 响应，列表时只填充基础字段（id/name/phone/sex/clazzname/status），详情时调 `/detail` 获取完整数据（含 parents/clazzs/bornDate 等）。避免模型爆炸。

### 7. 学生 API 服务层

按照现有模式：`StudentManageService`(interface) → `StudentManageServiceImpl`(CompletableFuture 包装) → `StudentApiServiceImpl`(extends OkHttpApiServiceImpl，实际 HTTP 调用)。

## Risks / Trade-offs

| 风险 | 缓解措施 |
|------|---------|
| 班级卡片页的 FlowPane 卡片布局无现有代码参考 | 使用 AtlantaFX `Card` 控件 + `FlowPane`，参考 AtlantaFX 文档和 demo |
| 当日出勤率需额外接口拼合 | `/attend/countClazzDay` 返回全校班级考勤，按 clazzId 匹配到对应卡片 |
| `StudentVO` 字段映射工作量大（API 响应结构复杂，含嵌套对象） | 按需映射：列表字段少，详情字段全；嵌套对象用内部静态类 |
| 详情页编辑模式下大量字段的 visible 绑定代码重复 | 考虑封装 `FormField` 组件，统一 Label+TextField 对 |
