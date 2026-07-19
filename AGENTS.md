# JavaFX MVVM 应用

## 构建与运行
```bash
mvn compile                  # 编译
mvn test                     # 运行测试 (JUnit 5 + Mockito)
mvn javafx:run               # 运行应用
```

## 架构
- **入口**: `com.example.app.MainApp` (JavaFX Application)
- **模式**: MVVM，通过 `AppContext` 手动实现依赖注入（服务注册表）
- **导航**: `ViewManager` (FXML 缓存) + `Router` (页面栈)

## 关键约定
- Controller 通过 FXML 设置；ViewModel 通过 `setViewModel()` 手动注入
- 服务在 `MainApp.initializeServices()` 中注册到 `AppContext`
- FXML 文件路径: `/fxml/{pageId}.fxml`，通过 `ViewManager.load(pageId)` 加载
- 使用 Lombok，需启用注解处理

## UI 框架规范
- **优先使用 AtlantaFX**: 项目已引入 `atlantafx-base` 依赖，UI 组件优先使用 AtlantaFX 提供的样式和控件
- **常用样式类**:
  - `Styles.ACCENT` - 主要操作按钮
  - `Styles.BUTTON_OUTLINED` - 边框按钮
  - `Styles.DANGER` - 危险/删除操作
  - `Styles.SUCCESS` / `Styles.WARNING` - 状态指示
  - `Styles.BADGE` - 标签/徽章
  - `Styles.SMALL` - 小尺寸控件
  - `Styles.ROUNDED` - 圆角输入框
- **控件**: 优先使用 `atlantafx.base.controls` 包下的控件，如 `Card`, `ModalPane` 等
- **主题**: 使用 `atlantafx.base.theme` 包下的主题类，如 `PrimerLight`, `PrimerDark`, `CupertinoLight` 等

## 导航配置规范
- **配置文件**: `navigation.yaml` 定义导航菜单结构
- **showInNav 属性**: 控制节点是否在导航栏显示
  - `showInNav: true` (默认) - 在导航栏显示
  - `showInNav: false` - 不在导航栏显示，但仍参与路由匹配
- **页面内部元素**: 页面内的Tab或子页面应设置 `showInNav: false`
  - 例如：用户详情页的"基本信息"、"操作记录"等Tab项
- **路由注册**: 所有节点都会注册到路由系统，不受 `showInNav` 影响

## 表单验证规范
- **统一使用 ValidatorFX**: 项目已引入 `validatorfx` 依赖（`net.synedra:validatorfx:1.0.0`），所有表单验证必须使用此框架
- **禁止**: 不得在 ViewModel 中手写 if-else 验证逻辑，不得使用旧的 `FieldValidator`/`FormValidator`
- **架构分层**:
  - **ViewModel 层**: 持有 `Validator` 实例，定义 Check 规则（`dependsOn` + `withMethod`），暴露 `validator.containsErrorsProperty()` 供 Controller 绑定
  - **Controller 层**: 调用 `validator.createCheck().decorates(node)` 绑定视觉装饰，绑定按钮禁用状态
- **基本用法**:
  ```java
  // ViewModel 中
  Validator validator = new Validator();
  validator.createCheck()
      .dependsOn("name", nameProperty)
      .withMethod(c -> {
          String name = c.get("name");
          if (name == null || name.isBlank()) c.error("名称不能为空");
      });
  // 暴露给 Controller
  public Validator getValidator() { return validator; }
  public BooleanBinding validProperty() { return validator.containsErrorsProperty().not(); }

  // Controller 中
  viewModel.getValidator().createCheck()
      .dependsOn("name", nameField.textProperty())
      .withMethod(c -> { /* 同上规则，或复用VM的Check */ })
      .decorates(nameField)
      .immediate();
  saveBtn.disableProperty().bind(viewModel.getValidator().containsErrorsProperty());
  ```
- **验证时机**:
  - 需即时反馈的字段使用 `.immediate()`
  - 提交时验证使用 `validator.validate()` 手动触发
  - 首次输入清除错误可使用 `.immediateClear()`
- **跨字段验证**: 一个 Check 可 `dependsOn` 多个 Property
  ```java
  validator.createCheck()
      .dependsOn("password", passwordProperty)
      .dependsOn("confirm", confirmProperty)
      .withMethod(c -> {
          if (!c.get("password").equals(c.get("confirm"))) c.error("两次密码不一致");
      });
  ```
- **装饰器自定义**: 使用 `DefaultDecoration.setFactory()` 适配 AtlantaFX 样式，避免默认装饰与主题冲突
- **禁用按钮提示**: 使用 `TooltipWrapper` 包裹提交按钮，hover 时显示验证错误原因

## Git 版本控制
- **每次代码变更后必须提交git**，便于版本回退
- 提交前先编译验证（`mvn compile`），确保代码无编译错误
- 提交信息应简洁描述变更内容

## 测试
- 单元测试使用 Mockito 模拟，无需 JavaFX 运行时
- 集成测试 `RealApiIntegrationTest` 调用真实 API（可能需要网络）

## 测试账号
- **手机号**: `15828245173`
- **密码**: `351688`
- 用于登录测试和API调用验证
