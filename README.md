# JavaFX MVVM Demo

一个基于 JavaFX 的 MVVM 架构示例项目，展示了现代 Java 客户端应用的最佳实践。

## 项目特性

### 架构设计
- **MVVM 架构**：清晰的 Controller-ViewModel-Service 分层
- **依赖注入**：使用 Google Guice 实现依赖管理
- **异步处理**：统一的线程池管理，避免直接创建线程

### 核心功能
- **用户认证**：登录功能，RSA 加密密码，Token 管理
- **主界面 Shell**：Tab 多页面管理，菜单导航
- **表单功能**：完整的表单校验系统
- **用户信息**：加载并显示当前用户信息

### UI 服务
- `DialogService`：模态弹窗
- `LoadingService`：加载遮罩层
- `ToastService`：消息提示
- `ThemeService`：主题切换（明/暗）

### 国际化
- 支持中文/英文切换
- 基于 ResourceBundle 实现

## 技术栈

- Java 17
- JavaFX 20
- Google Guice 7.0（依赖注入）
- OkHttp 4.9（网络请求）
- Gson 2.8（JSON 处理）
- Lombok（代码简化）
- Logback（日志）
- JUnit 5 + Mockito（测试）

## 项目结构

```
src/main/java/com/example/app/
├── config/                 # 配置模块
│   └── AppModule.java      # Guice 模块配置
├── controller/             # 控制器层
│   ├── LoginController.java
│   ├── ShellController.java
│   ├── HomeController.java
│   ├── FormController.java
│   └── ListController.java
├── viewmodel/              # ViewModel 层
│   ├── ViewModelBase.java
│   ├── LoginViewModel.java
│   └── ShellViewModel.java
├── service/                # 服务层接口
│   ├── AuthService.java
│   ├── UserService.java
│   ├── DialogService.java
│   ├── LoadingService.java
│   └── ToastService.java
├── service/impl/           # 服务层实现
│   ├── AuthServiceImpl.java
│   └── UserServiceImpl.java
├── api/                    # API 层
│   ├── ApiService.java
│   ├── ApiException.java
│   └── okhttp/             # OkHttp 实现
├── model/                  # 数据模型
│   ├── User.java
│   ├── Student.java
│   └── ...
├── executor/               # 异步执行器
│   └── AsyncExecutor.java
├── exception/              # 异常处理
│   └ ExceptionHandler.java
├── i18n/                   # 国际化
│   └ I18nService.java
├── storage/                # 存储
│   └ TokenStorage.java
├── validator/              # 表单校验
│   ├── FormValidator.java
│   └ FieldValidator.java
├── component/              # UI 组件
│   └ FormItem.java
├── MainApp.java            # 应用入口
├── AppContext.java         # 应用上下文
├── ViewManager.java        # 视图管理
├── StageManager.java       # Stage 管理
└── ThemeService.java       # 主题服务

src/main/resources/
├── fxml/                   # FXML 文件
├── css/                    # CSS 样式
├── application.properties  # 配置文件
├── messages_zh_CN.properties # 中文资源
├── messages_en_US.properties # 英文资源
```

## 运行项目

### 前置要求
- JDK 17+
- Maven 3.6+

### 运行命令
```bash
mvn javafx:run
```

### 编译打包
```bash
mvn clean package
```

## 运行测试

```bash
mvn test
```

## 配置说明

编辑 `src/main/resources/application.properties`：

```properties
api.host=https://api.example.com
api.timeout=30000
retry.max=5
retry.sleep=1000
```

## 国际化

项目支持中英文切换，资源文件位于：
- `messages_zh_CN.properties` - 中文
- `messages_en_US.properties` - 英文

## 依赖注入

使用 Google Guice 管理依赖，配置见 `AppModule.java`：

```java
public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApiService.class).to(OkHttpApiServiceImpl.class).in(Singleton.class);
        bind(AuthService.class).to(AuthServiceImpl.class).in(Singleton.class);
        bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
    }
}
```

## 最佳实践

1. **异步处理**：使用 `AsyncExecutor` 统一管理线程池
2. **异常处理**：使用 `ExceptionHandler` 统一记录日志
3. **表单校验**：使用 `FormValidator` + `FieldValidator` 组合
4. **国际化**：所有 UI 文本通过 `I18nService` 获取

## 许可证

MIT License