## Why

当前应用缺少学生管理功能，无法满足教育类业务场景的核心需求。需要新增完整的学生管理模块，包括班级概览、学生列表、学生详情、新增/编辑等功能，同时重构导航结构以容纳新模块。

## What Changes

- **导航重构**: 删除"业务"示例模块；"系统"模块仅保留"系统配置"；"员工管理"、"角色管理"、"API监控"提升到与"系统"平级
- **新增"学生管理"模块**: 作为顶层导航项，点击直接进入班级卡片概览页
- **班级卡片概览页**: 以卡片形式展示班级信息（班主任、在读/登记/请假人数、空位、今日出勤率）
- **班级学生列表**: 点击班级卡片进入分页列表，支持新增学生（弹窗）
- **全局搜索**: 在班级卡片页顶部提供搜索框，可搜索全校学生（姓名/手机号/拼音）
- **学生详情页**: 分组展示基本信息、考勤信息、财务信息、班级变动历史
- **学生编辑**: 在详情页内原地编辑，不可调班
- **新增学生**: 弹窗录入（姓名、性别、生日、电话、地址、班级、联系人信息）

## Capabilities

### New Capabilities
- `class-card-overview`: 班级卡片概览页，展示班级信息及统计、当日出勤率
- `global-student-search`: 全校学生全局搜索，支持关键词模糊匹配
- `class-student-list`: 按班级查看学生分页列表
- `student-detail-view`: 学生详情页，以分组区块展示基本信息/考勤/财务/班级历史
- `student-inline-edit`: 详情页内原地编辑学生信息（不含调班）
- `student-create-modal`: 弹窗新增学生，提交后刷新列表

### Modified Capabilities
- (无现有 spec 需要修改)

## Impact

- **导航**: `navigation.yaml` 重构，影响 `ShellController` 的导航树
- **新增文件**: 约 18 个 Java 文件、3 个 FXML 文件、i18n properties
- **修改文件**: `AppModule.java` (Guice 绑定)、`navigation.yaml`、`messages_*.properties`
- **API 依赖**: `GET /org/clazz/listWithCount`, `GET /attend/countClazzDay`, `GET /student/listWithClazz`, `GET /student/detail`, `POST /student/create`, `POST /student/change`, `GET /student/listByCondition` 等
- **模型层**: 新增 `StudentVO`（替换现有占位 `Student.java`）、`ClazzWithCountVO`、`StudentFO` 等
