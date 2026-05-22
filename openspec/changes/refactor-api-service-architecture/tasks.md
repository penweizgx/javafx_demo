## 1. BaseApiServiceImpl 泛型提取方法

- [x] 1.1 Add extractResBodyAs(String response, Class<T> beanClass) method to BaseApiServiceImpl — extracts resbody and deserializes to target bean class
- [x] 1.2 Add extractResBodyAs(String response, Type targetType) method to BaseApiServiceImpl — supports generic types like List<T>
- [x] 1.3 Handle edge cases: null/missing resbody → ApiException, code != 200 → ApiException with message

## 2. AuthApiServiceImpl

- [x] 2.1 Create AuthApiServiceImpl extending OkHttpApiServiceImpl with login(username, password) method
- [x] 2.2 Remove login() and getCurrentUser() from OkHttpApiServiceImpl (getCurrentUser暂保留在OkHttpApiServiceImpl中，后续迁移到UserApiServiceImpl)
- [x] 2.3 Refactor AuthServiceImpl to inject AuthApiServiceImpl instead of ApiService

## 3. EmployeeApiServiceImpl

- [x] 3.1 Create EmployeeApiServiceImpl extending OkHttpApiServiceImpl with listEmployees(EmployeeListReq) method
- [x] 3.2 Implement mapDTOtoEmployee() for nested JSON → flat Employee mapping
- [x] 3.3 Use extractResBodyAs() to parse response, then map each DTO item to Employee bean
- [x] 3.4 Refactor EmployeeManageServiceImpl to inject EmployeeApiServiceImpl, remove direct HTTP/JSON parsing code

## 4. Guice Configuration

- [x] 4.1 Add AuthApiServiceImpl and EmployeeApiServiceImpl bindings in AppModule

## 5. Verification

- [x] 5.1 Run mvn compile to verify no compilation errors