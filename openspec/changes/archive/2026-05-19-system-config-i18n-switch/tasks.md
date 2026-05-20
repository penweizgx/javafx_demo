## 1. I18nService 增强

- [x] 1.1 在 I18nService 中添加 Preferences 持久化方法（saveLocalePreference / loadLocalePreference）
- [x] 1.2 修改 I18nService.setLocale()，切换后自动保存偏好并发布 LocaleChangeEvent
- [x] 1.3 修改 I18nService 构造函数，初始化时从 Preferences 恢复语言偏好
- [x] 1.4 创建 LocaleChangeEvent 事件类（包含 newLocale 字段）

## 2. 系统配置页面

- [x] 2.1 创建 system_config.fxml（使用 AtlantaFX Card + ComboBox 布局）
- [x] 2.2 创建 SystemConfigViewModel（持有语言选择状态，调用 I18nService 切换）
- [x] 2.3 创建 SystemConfigController（管理控件引用，监听 LocaleChangeEvent 刷新文本）
- [x] 2.4 在 navigation.yaml 中注册 system-config 路由节点（/system/config）

## 3. 国际化文本

- [x] 3.1 在 messages_zh_CN.properties 中添加系统配置页面相关文本键
- [x] 3.2 在 messages_en_US.properties 中添加系统配置页面相关文本键

## 4. Shell 界面刷新

- [x] 4.1 ShellController 订阅 LocaleChangeEvent，收到后刷新所有 i18n 文本（品牌名、按钮、版权等）
- [x] 4.2 ShellController 中 btnSystem 点击事件改为导航到 /system/config

## 5. DI 注册与编译验证

- [x] 5.1 在 AppModule 中绑定 SystemConfigViewModel（如需要）
- [x] 5.2 运行 mvn compile 验证编译通过