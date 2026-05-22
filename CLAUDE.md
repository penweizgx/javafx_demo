# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在本仓库中工作时提供指导。

## 构建与运行命令

```bash
mvn compile          # 编译（提交前运行以验证无错误）
mvn test             # 运行所有测试（JUnit 5 + Mockito）
mvn javafx:run       # 运行应用
mvn clean package    # 构建 JAR
```

运行单个测试类：`mvn test -Dtest=LoginViewModelTest`
运行单个测试方法：`mvn test -Dtest=LoginViewModelTest#testLoginSuccess`

## 架构

**模式：** MVVM + Google Guice 7.0 依赖注入。

**入口：** `MainApp.java` — 从 `AppModule` 创建 Guice Injector，初始化 `AppContext`（单例 Injector 包装器），然后显示登录界面。登录成功后，`showMainShell()` 加载 shell.fxml 并连接 Router、各服务和 ViewManager。

**依赖注入流程：**
- `AppModule` 绑定所有服务（ApiService→OkHttpApiServiceImpl、AuthService、UserService、UserManageService→MockUserManageService、TokenStorage、I18nService、RouteRegistry）
- `AppContext.get().getService(Class)` 提供全局服务访问
- `ViewManager` 使用 `FXMLLoader.setControllerFactory(injector::getInstance)` 使 FXML 控制器由 Guice 管理
- 控制器通过手动 `setViewModel()` 调用接收 ViewModel（非 Guice 注入）

**导航/路由系统：**
- `Router` — 类 SPA 路由器，基于 TabPane 显示，支持路由模式匹配（`:param` 变量如 `/system/user/detail/:id`）、浏览器式历史记录（前进/后退）和导航守卫
- `RouteRegistry` — 模式→正则匹配，支持参数提取
- `NavigationConfig` — 从 `navigation.yaml` 加载（SnakeYAML），在 `NavigationPane` 中渲染为 TreeView
- `AuthGuard` — 未认证时重定向到 `/login`（公开路由：`/login`、`/home`）
- `EventBus` — 单例发布/订阅，用于 `RouteChangeEvent` 和 `NavigationClickEvent`
- 接收路由参数的控制器实现 `Router.ParamReceiver`

**异步执行：**
- `AsyncExecutor` — 固定线程池（CPU 核心数），所有 `CompletableFuture` 任务通过此处执行
- `ViewModelBase.executeAsync()` — 包装异步工作，通过 `Platform.runLater` 保证 UI 线程安全

**API 层：**
- `ApiService` 接口 → `OkHttpApiServiceImpl`（RSA 密码加密、令牌管理、指数退避重试）
- `RequestExecutor` 策略模式：`SimpleGetRequestExecutor`、`FormPostRequestExecutor`、`JsonPostRequestExecutor`、`FileUploadRequestExecutor`
- `InMemoryConfigStorage` 存储令牌/密钥/代理；`HostConfig` 默认为 `https://doixiao.cn/api`

## 关键约定

- **Lombok 必需** — 必须启用注解处理；模型使用 `@Data`
- **FXML 路径约定**：`/fxml/{pageId}.fxml`，通过 `ViewManager.load(pageId)` 加载
- **UI 框架**：AtlantaFX（`atlantafx-base`）— 优先使用其样式类（`Styles.ACCENT`、`Styles.DANGER`、`Styles.BUTTON_OUTLINED` 等）和控件（`Card`、`ModalPane`），而非原生 JavaFX 等价物。AtlantaFX 源码位于 `/Users/penwei/Downloads/atlantafx-2.0.1`，使用不明确时可查阅参考
- **主题**：`ThemeService` 切换 light.css/dark.css；AtlantaFX `PrimerLight` 作为基础用户代理样式表
- **国际化**：所有 UI 文本通过 `I18nService.getString(key)` 获取 — 资源包为 `messages_zh_CN.properties` 和 `messages_en_US.properties`
- **导航 YAML**：`showInNav` 控制导航栏可见性；所有节点无论是否显示都注册到路由。页面内子标签页设置 `showInNav: false`
- **Git**：每次代码变更后提交；提交前务必运行 `mvn compile`
- **令牌存储**：使用 `java.util.prefs.Preferences`（非文件方式）
- **api接口文档**：api-docs.json
## 测试账号

- 手机号：`15828245173`，密码：`351688` — 用于登录/API 测试

## 潜在依赖缺失

SnakeYAML（`NavigationConfigLoader` 使用）和 Ikonli/Material2 图标（导航组件使用）未在 pom.xml 中显式声明 — 它们可能是 AtlantaFX 的传递依赖，但如果该依赖变更可能导致运行时问题。
