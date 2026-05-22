## ADDED Requirements

### Requirement: BaseApiServiceImpl provides generic resbody extraction and deserialization
BaseApiServiceImpl SHALL provide an extractResBodyAs(String response, Class<T> beanClass) method that extracts the resbody from the API response envelope, checks the code field, and deserializes the resbody into the specified bean class. It SHALL also provide an extractResBodyAs(String response, Type targetType) variant for generic types like List<T>.

#### Scenario: Extract resbody as single bean
- **WHEN** extractResBodyAs(response, Employee.class) is called and the response contains `{code: 200, resbody: {id: 123, name: {...}}}`
- **THEN** the method SHALL check code=200, extract resbody JsonObject, and deserialize it into an Employee bean

#### Scenario: Extract resbody as list
- **WHEN** extractResBodyAs(response, TypeToken.getParameterized(List.class, Employee.class).getType()) is called and the response contains `{code: 200, resbody: [{...}, {...}]}`
- **THEN** the method SHALL check code=200, extract resbody JsonArray, and deserialize it into a List<Employee>

#### Scenario: API returns error code
- **WHEN** the response code is not 200
- **THEN** extractResBodyAs SHALL throw ApiException with the message and code from the response

#### Scenario: resbody is null
- **WHEN** resbody field is absent or JsonNull
- **THEN** extractResBodyAs SHALL throw ApiException indicating empty response body

### Requirement: AuthApiServiceImpl provides login HTTP call
AuthApiServiceImpl SHALL inherit OkHttpApiServiceImpl and provide a login(username, password) method that sends the login request using OkHttp directly (form post with RSA-encrypted password), and uses extractResBody() to validate the response and extract the auth token from headers.

#### Scenario: Successful login
- **WHEN** login(username, password) is called
- **THEN** AuthApiServiceImpl SHALL call initRSAKey(), encrypt password using RSA, send POST request to ApiUrl.Authenticate.LOGIN_WITH_PASSWORD, validate via extractResBody(), and store the x-auth-token in configStorage

#### Scenario: RSA key not initialized
- **WHEN** initRSAKey() fails and modulus/exponent are null
- **THEN** login SHALL throw ApiException indicating RSA key initialization failure

### Requirement: AuthServiceImpl uses AuthApiServiceImpl for login
AuthServiceImpl SHALL inject AuthApiServiceImpl and call authApiService.login(username, password) for authentication.

#### Scenario: Login via AuthApiServiceImpl
- **WHEN** AuthServiceImpl.login(username, password) is called
- **THEN** it SHALL delegate to AuthApiServiceImpl.login(username, password)

### Requirement: OkHttpApiServiceImpl is pure HTTP infrastructure
OkHttpApiServiceImpl SHALL NOT contain any business methods. It SHALL only provide HTTP infrastructure: initHttp(), getRequestHttpClient, getRequestHttpProxy.

#### Scenario: No business methods in OkHttpApiServiceImpl
- **WHEN** reviewing OkHttpApiServiceImpl's public methods
- **THEN** only initHttp, getRequestHttpClient, getRequestHttpProxy SHALL exist — no login or getCurrentUser