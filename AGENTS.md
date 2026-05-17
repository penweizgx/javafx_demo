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
