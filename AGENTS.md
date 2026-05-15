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

## 测试
- 单元测试使用 Mockito 模拟，无需 JavaFX 运行时
- 集成测试 `RealApiIntegrationTest` 调用真实 API（可能需要网络）
