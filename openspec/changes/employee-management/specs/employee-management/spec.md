## ADDED Requirements

### Requirement: Employee data model
The system SHALL define an Employee model with fields: id (Long), name (String), gender (String), birthday (String), phone (String), email (String), orgName (String), hireDate (String), status (Integer where 1=active, 0=inactive).

#### Scenario: Employee model creation
- **WHEN** the application creates an Employee instance
- **THEN** all fields SHALL be accessible via getters/setters (Lombok @Data)

### Requirement: Employee list API integration
The system SHALL call POST `/api/employee/list` with an EmployeeListReq body (pageNum, pageSize, name, orgName) and parse the response into a PageResult containing a list of Employee records.

#### Scenario: Successful employee list fetch
- **WHEN** the system sends a POST request to `/api/employee/list` with valid parameters
- **THEN** the response SHALL be parsed into PageResult with records, total, pageNum, pageSize

#### Scenario: Employee list with search filters
- **WHEN** name and/or orgName parameters are provided in the request
- **THEN** the API SHALL return filtered results matching the search criteria

#### Scenario: Employee list with pagination
- **WHEN** pageNum and pageSize parameters are provided
- **THEN** the API SHALL return the corresponding page of results with total count

### Requirement: EmployeeManageService
The system SHALL provide an EmployeeManageService interface with a `listEmployees(EmployeeListReq)` method returning `CompletableFuture<PageResult<Employee>>`, implemented by EmployeeManageServiceImpl using ApiService and AsyncExecutor.

#### Scenario: Service delegates to API
- **WHEN** listEmployees is called with an EmployeeListReq
- **THEN** the implementation SHALL use ApiService to send the POST request and AsyncExecutor for async execution

### Requirement: Employee list view model
The system SHALL provide an EmployeeListViewModel extending ViewModelBase, holding ObservableList<Employee>, pagination state, and search conditions, with a loadEmployees() method that triggers async query.

#### Scenario: Load employees on demand
- **WHEN** loadEmployees() is called
- **THEN** the ViewModel SHALL call EmployeeManageService.listEmployees and update the ObservableList on success

#### Scenario: Search by name
- **WHEN** a name search term is provided and loadEmployees() is called
- **THEN** the ViewModel SHALL include the name parameter in the request

#### Scenario: Pagination navigation
- **WHEN** the user navigates to next/previous page
- **THEN** the ViewModel SHALL update pageNum and call loadEmployees() with the new page number

### Requirement: Employee list view
The system SHALL display an employee list page with a search bar (name TextField + search Button), a TableView with columns (name, gender, phone, email, org, hire date, status), and pagination controls (previous/next buttons + page indicator).

#### Scenario: Display employee data in table
- **WHEN** employees are loaded
- **THEN** the TableView SHALL display each employee's name, gender, phone, email, orgName, hireDate, and status

#### Scenario: Search employees by name
- **WHEN** the user enters a name in the search field and clicks search
- **THEN** the system SHALL filter employees by the entered name

#### Scenario: Navigate pages
- **WHEN** the user clicks next/previous page button
- **THEN** the system SHALL load and display the corresponding page of employees

#### Scenario: Status display
- **WHEN** an employee has status 1
- **THEN** the status column SHALL display "在职" (zh) / "Active" (en)
- **WHEN** an employee has status 0
- **THEN** the status column SHALL display "离职" (zh) / "Inactive" (en)

### Requirement: Employee list i18n support
The system SHALL support locale switching for all employee list UI text (column headers, button labels, status values) via I18nService.

#### Scenario: Switch locale refreshes table headers
- **WHEN** the locale is changed
- **THEN** all employee list column headers and labels SHALL update to the new locale

### Requirement: Navigation update for employee management
The system SHALL replace the "用户管理->用户列表" navigation node with "员工管理->员工列表", routing to `/system/employee/list` with FXML `employee_list`.

#### Scenario: Navigation shows employee list
- **WHEN** the user clicks "员工列表" in the navigation tree
- **THEN** the system SHALL navigate to `/system/employee/list` and display the employee list page

### Requirement: Guice binding for EmployeeManageService
The system SHALL bind EmployeeManageService to EmployeeManageServiceImpl in AppModule.

#### Scenario: Service injection
- **WHEN** a controller or ViewModel requests EmployeeManageService via AppContext
- **THEN** an EmployeeManageServiceImpl instance SHALL be provided
