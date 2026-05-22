## ADDED Requirements

### Requirement: EmployeeApiServiceImpl provides employee list HTTP call with bean deserialization
EmployeeApiServiceImpl SHALL inherit OkHttpApiServiceImpl and provide a listEmployees(EmployeeListReq) method that sends a GET request to `/employee/list`, uses extractResBodyAs() with the target type to deserialize the resbody array directly into a List of flat Employee beans, handling nested DTO → flat model mapping via a custom mapping method.

#### Scenario: Successful employee list fetch with automatic deserialization
- **WHEN** listEmployees(req) is called with valid search parameters
- **THEN** EmployeeApiServiceImpl SHALL use ApiUrl.Employee.LIST.getUrl() to construct the URL, pass search params as query parameters, call apiService.get(), and use extractResBodyAs(response, targetType) to extract and deserialize the resbody array into business beans

#### Scenario: Nested JSON field mapping handled in ApiServiceImpl
- **WHEN** the resbody contains nested PersonName, BirthdayDate, DatePeriod objects
- **THEN** EmployeeApiServiceImpl SHALL provide a mapEmployeeDTOtoEmployee() method that converts each EmployeeDTO JsonObject to a flat Employee bean (name from PersonName.name, birthday from BirthdayDate.date, hireDate from DatePeriod.fromDate, sex int → gender string, active boolean → status int)

#### Scenario: API returns error code
- **WHEN** the response code is not 200
- **THEN** extractResBodyAs SHALL throw ApiException with the error message

### Requirement: EmployeeManageServiceImpl uses EmployeeApiServiceImpl
EmployeeManageServiceImpl SHALL inject EmployeeApiServiceImpl and call employeeApiService.listEmployees(req) to fetch employee data, handling only async execution and error handling. No JSON parsing code SHALL remain in EmployeeManageServiceImpl.

#### Scenario: Employee data loading via ApiServiceImpl
- **WHEN** EmployeeManageService.listEmployees(req) is called
- **THEN** EmployeeManageServiceImpl SHALL delegate HTTP + JSON parsing to EmployeeApiServiceImpl.listEmployees(req) via CompletableFuture.supplyAsync, and wrap any exceptions