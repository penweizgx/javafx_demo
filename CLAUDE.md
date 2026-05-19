# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
mvn compile          # Compile (run before committing to verify no errors)
mvn test             # Run all tests (JUnit 5 + Mockito)
mvn javafx:run       # Run the application
mvn clean package    # Build JAR
```

Run a single test class: `mvn test -Dtest=LoginViewModelTest`
Run a single test method: `mvn test -Dtest=LoginViewModelTest#testLoginSuccess`

## Architecture

**Pattern:** MVVM with Google Guice 7.0 dependency injection.

**Entry point:** `MainApp.java` — creates Guice Injector from `AppModule`, initializes `AppContext` (singleton Injector wrapper), then shows login screen. After login, `showMainShell()` loads shell.fxml and wires up Router, services, and ViewManager.

**DI flow:**
- `AppModule` binds all services (ApiService→OkHttpApiServiceImpl, AuthService, UserService, UserManageService→MockUserManageService, TokenStorage, I18nService, RouteRegistry)
- `AppContext.get().getService(Class)` provides global service access
- `ViewManager` uses `FXMLLoader.setControllerFactory(injector::getInstance)` so FXML controllers are Guice-managed
- Controllers receive ViewModels via manual `setViewModel()` calls (not Guice injection)

**Navigation/Routing system:**
- `Router` — SPA-like router with TabPane display, route pattern matching (`:param` variables like `/system/user/detail/:id`), browser-like history (back/forward), and navigation guards
- `RouteRegistry` — pattern→regex matching with param extraction
- `NavigationConfig` — loaded from `navigation.yaml` (SnakeYAML), rendered as TreeView in `NavigationPane`
- `AuthGuard` — redirects to `/login` if not authenticated (public routes: `/login`, `/home`)
- `EventBus` — singleton pub/sub for `RouteChangeEvent` and `NavigationClickEvent`
- Controllers receiving route params implement `Router.ParamReceiver`

**Async execution:**
- `AsyncExecutor` — fixed thread pool (CPU core count), all `CompletableFuture` tasks go through here
- `ViewModelBase.executeAsync()` — wraps async work with `Platform.runLater` for UI thread safety

**API layer:**
- `ApiService` interface → `OkHttpApiServiceImpl` (RSA password encryption, token management, retry with exponential backoff)
- `RequestExecutor` strategy pattern: `SimpleGetRequestExecutor`, `FormPostRequestExecutor`, `JsonPostRequestExecutor`, `FileUploadRequestExecutor`
- `InMemoryConfigStorage` holds tokens/keys/proxy; `HostConfig` defaults to `https://doixiao.cn/api`

## Key Conventions

- **Lombok required** — annotation processing must be enabled; models use `@Data`
- **FXML path convention**: `/fxml/{pageId}.fxml`, loaded via `ViewManager.load(pageId)`
- **UI framework**: AtlantaFX (`atlantafx-base`) — use its style classes (`Styles.ACCENT`, `Styles.DANGER`, `Styles.BUTTON_OUTLINED`, etc.) and controls (`Card`, `ModalPane`) over raw JavaFX equivalents
- **Theme**: `ThemeService` switches light.css/dark.css; AtlantaFX `PrimerLight` as base user agent stylesheet
- **i18n**: All UI text via `I18nService.getString(key)` — resource bundles `messages_zh_CN.properties` and `messages_en_US.properties`
- **Navigation YAML**: `showInNav` controls nav bar visibility; all nodes register in routing regardless. Sub-tabs within pages set `showInNav: false`
- **Git**: Commit after each code change; always `mvn compile` before committing
- **Token storage**: `java.util.prefs.Preferences` (not file-based)

## Test Account

- Phone: `15828245173`, Password: `351688` — for login/API testing

## Potential Dependency Gaps

SnakeYAML (used by `NavigationConfigLoader`) and Ikonli/Material2 icons (used by nav components) are not explicitly declared in pom.xml — they may be transitive from AtlantaFX but could cause runtime issues if that dependency changes.