## Context

当前OkHttpApiServiceImpl承担了两个职责：HTTP基础设施（initHttp构建OkHttpClient、拦截器配置）和认证业务（login方法）。其他业务模块（EmployeeManageServiceImpl等）通过注入ApiService接口直接调用get/postJSON，但需要自行拼接URL、手动解析JSON响应、处理异常，没有复用BaseApiServiceImpl的extractResBody（统一code检查+resbody提取）、execute（重试机制）等能力。

继承链：OkHttpApiServiceImpl → BaseApiServiceImpl → ApiService接口

BaseApiServiceImpl已提供的能力：
- `get(url, queryParams)` / `postJSON(url, params)` / `post(url, params)` — 通过RequestExecutor执行HTTP请求
- `execute(executor, uri, data)` — 统一重试机制（5次指数退避）
- `extractResBody(responseContent)` — 解析 `{code, message, resbody}` 格式，检查code=200，返回resbody JsonObject
- `configStorage` — 令牌、RSA密钥等配置存储
- `initRSAKey()` — 初始化RSA公钥

当前痛点：每个业务Service都要手动做两件事——1) 调用apiService.get/postJSON拿到String，2) 自己解析JSON提取resbody再转业务Bean。这个过程在BaseApiServiceImpl的extractResBody()已经做了第一步（提取resbody JsonObject），但第二步（JsonObject → 业务Bean）每个Service都在重复实现。

## Goals / Non-Goals

**Goals:**
- 在BaseApiServiceImpl新增泛型方法，统一完成 resbody提取 + JSON反序列化 → 业务Bean，业务ApiServiceImpl只需传入bean class即可获得业务对象
- 每个业务模块的API调用收拢到独立的ApiServiceImpl，继承OkHttpApiServiceImpl复用HTTP基础设施和新增的泛型提取方法
- Service层仅做业务逻辑编排（异步执行、错误处理），不再涉及JSON解析
- OkHttpApiServiceImpl回归纯粹的HTTP基础设施角色

**Non-Goals:**
- 不重构ApiService接口本身（保留get/postJSON/login/getCurrentUser等方法签名）
- 不改造RequestExecutor体系
- 不处理UserManageService（MockUserManageService仍在使用Mock数据，暂不涉及真实API）

## Decisions

### D1: BaseApiServiceImpl新增泛型extractResBodyAs方法

**选择**: 在BaseApiServiceImpl新增 `extractResBodyAs(String response, Type targetType)` 方法，完成 resbody JsonObject → 业务Bean 的统一转换

**理由**: 当前extractResBody()只返回JsonObject，后续Bean转换在每个Service里重复。新增泛型方法让业务ApiServiceImpl只需一行调用即可获得目标Bean，彻底消除JSON解析代码的散落。

**方法设计**:
```java
protected <T> T extractResBodyAs(String response, Class<T> beanClass) throws ApiException {
    JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
    // code check (复用现有逻辑)
    if (jsonObject.has("code") && jsonObject.get("code").getAsInt() != 200) {
        throw new ApiException(jsonObject.get("message").getAsString(), jsonObject.get("code").getAsInt());
    }
    JsonElement resbody = jsonObject.get("resbody");
    return gson.fromJson(resbody, beanClass);
}

protected <T> T extractResBodyAs(String response, Type targetType) throws ApiException {
    // 同上，支持List<Employee>等泛型类型
}
```

**替代方案**: 在每个ApiServiceImpl各自实现解析 → 重复代码，违背复用目标。

### D2: 业务ApiServiceImpl继承OkHttpApiServiceImpl

**选择**: 继承OkHttpApiServiceImpl

**理由**: OkHttpApiServiceImpl是Guice绑定的Singleton实例，已包含完整的HttpClient配置。业务子类继承即可复用httpClient、configStorage、extractResBodyAs()、get/postJSON等全部能力。

**替代方案**: 继承BaseApiServiceImpl → 需要重新配置HttpClient，违背复用目标。

### D3: 业务ApiServiceImpl作为Guice Singleton

**选择**: 每个ApiServiceImpl独立绑定到AppModule

**理由**: 与现有OkHttpApiServiceImpl绑定模式一致。Guice创建子类实例时自动调用父类@Inject构造器，initHttp()正常完成。

### D4: Service层注入特化ApiServiceImpl而非通用ApiService

**选择**: 注入特化类型

**理由**: 每个业务模块只调用自己领域的API，明确依赖范围。

### D5: login()从OkHttpApiServiceImpl移到AuthApiServiceImpl

**选择**: 移到AuthApiServiceImpl

**理由**: login是认证业务，不属于HTTP基础设施。AuthApiServiceImpl继承后复用httpClient、configStorage、initRSAKey()、extractResBody()。

## Risks / Trade-offs

- [extractResBodyAs需要处理resbody为JsonArray的情况] → Gson.fromJson支持从JsonArray反序列化为List<T>，传入TypeToken.getParameterized即可
- [resbody字段为空/null的边界情况] → extractResBodyAs需检查resbody是否为JsonNull，抛出明确异常
- [Guice继承绑定需注意父类@Inject构造器] → 已验证可行
- [业务ApiServiceImpl继承形成类层级] → 当前模块数量少，层级可控