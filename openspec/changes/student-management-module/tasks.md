## 1. 导航重构

- [x] 1.1 删除 navigation.yaml 中 `business` 模块（表单示例、列表示例）
- [x] 1.2 "系统"模块精简：仅保留 `system-config`，移除 `employee-mgmt`、`role-mgmt`、`api-monitor`
- [x] 1.3 将 `employee-mgmt`、`role-mgmt`、`api-monitor` 提升为顶层导航项
- [x] 1.4 新增 `student-mgmt` 顶层导航项（含路径 /student/classes 及子路由）
- [x] 1.5 添加子路由：/student/class/:clazzId 和 /student/detail/:id（均 showInNav: false）

## 2. 数据模型层

- [x] 2.1 创建 `StudentVO`（含字段：id, name, nickname, sex, bornDate, phone, address, status, clazzId, clazzname, parents: ParentVO[], clazzs: StudentClazzHistoryVO[]）
- [x] 2.2 创建 `StudentFO`（对应 create/change 请求体）
- [x] 2.3 创建 `StudentQO`（查询条件：keyword, clazzId, includeGraduated, period）
- [x] 2.4 创建 `ClazzWithCountVO`（班级卡片数据：id, name, teachers[], capacity, vacancy, activeNum, leaveNum, regNum, status）
- [x] 2.5 创建 `ClazzOptionVO`（班级下拉选项：id, name）
- [x] 2.6 删除现有的占位 `Student.java`（或替换为 StudentVO）

## 3. API 服务层

- [x] 3.1 创建 `StudentApiServiceImpl`（extends OkHttpApiServiceImpl）
- [x] 3.2 实现 `listWithClazz(clazzId)` → GET /student/listWithClazz/{id}
- [x] 3.3 实现 `listByCondition(qo)` → GET /student/listByCondition
- [x] 3.4 实现 `detail(id)` → GET /student/detail/{id}
- [x] 3.5 实现 `create(fo)` → POST /student/create
- [x] 3.6 实现 `change(fo)` → POST /student/change
- [x] 3.7 实现 `groupClazzStudent(schId)` → GET /student/groupClazzStudent
- [x] 3.8 实现对 `/org/clazz/listWithCount` 和 `/org/clazz/listActive` 的调用
- [x] 3.9 实现对 `/attend/countClazzDay` 和 `/attend/listMonthAttend/{studentId}` 的调用

## 4. 服务层

- [x] 4.1 创建 `StudentManageService` 接口
- [x] 4.2 创建 `StudentManageServiceImpl`（CompletableFuture 包装 API 调用）
- [x] 4.3 在 `AppModule` 注册 Guice 绑定

## 5. 班级卡片概览页

- [x] 5.1 创建 `ClassCardsViewModel`（班级列表、今日考勤统计、搜索状态）
- [x] 5.2 创建 `ClassCardsController`（卡片布局、搜索交互、点击导航）
- [x] 5.3 创建 `student_classes.fxml`（顶部搜索框 + FlowPane 卡片区域）
- [x] 5.4 实现卡片组件（使用 AtlantaFX Card，展示班级信息和统计数字）
- [x] 5.5 实现考勤率与班级卡片的匹配逻辑

## 6. 班级学生列表页

- [x] 6.1 创建 `StudentListViewModel`（分页、加载、列表数据）
- [x] 6.2 创建 `StudentListController`（TableView、分页控件、新增/返回按钮）
- [x] 6.3 创建 `student_list.fxml`（顶部操作栏 + 表格 + 底部分页）
- [x] 6.4 实现双击行导航到详情页

## 7. 学生详情页

- [x] 7.1 创建 `StudentDetailViewModel`（学生数据、考勤、财务、编辑状态）
- [x] 7.2 创建 `StudentDetailController`（实现 ParamReceiver，绑定区块）
- [x] 7.3 创建 `student_detail.fxml`（顶部头像/名称/状态 + 四个信息区块 VBox）
- [x] 7.4 实现基本信息区块展示
- [x] 7.5 实现考勤信息区块展示（调月考勤接口）
- [x] 7.6 实现财务信息区块展示（调账户接口）
- [x] 7.7 实现班级变动历史区块展示（从 StudentVO.clazzs 取）

## 8. 学生详情页内编辑

- [x] 8.1 ViewModel 增加 editing 状态、editData 拷贝逻辑
- [x] 8.2 在 FXML 中为每个可编辑字段添加 Label + TextField/ComboBox 对，绑定 editing visible
- [x] 8.3 实现保存逻辑（POST /student/change，不含 clazzId）
- [x] 8.4 实现取消逻辑（还原原始数据）
- [x] 8.5 确保班级字段在编辑模式下保持只读显示

## 9. 新增学生弹窗

- [x] 9.1 创建 `StudentCreateModal` 组件（extends VBox，类似 EmployeeDetailModal）
- [x] 9.2 实现表单字段：姓名、性别(ComboBox)、生日(DatePicker)、电话、地址、班级(ComboBox)、联系人、联系人关系(ComboBox)
- [x] 9.3 实现班级下拉数据加载（/org/clazz/listActive）
- [x] 9.4 实现保存逻辑（POST /student/create）
- [x] 9.5 实现表单验证（姓名、电话为必填）
- [x] 9.6 在学生列表页的"新增学生"按钮绑定弹窗打开

## 10. 国际化

- [x] 10.1 在 `messages_zh_CN.properties` 添加学生管理模块所有 key
- [x] 10.2 在 `messages_en_US.properties` 添加对应英文翻译
- [x] 10.3 在所有 Controller 中绑定 i18n 刷新

## 11. 验证与提交

- [x] 11.1 运行 `mvn compile` 验证编译通过
- [x] 11.2 运行 `mvn test` 验证现有测试通过（测试编译失败为预先存在的问题，非本次变更导致）
- [x] 11.3 Git 提交
