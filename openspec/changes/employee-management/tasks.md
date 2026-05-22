## 1. Data Model & Service

- [x] 1.1 Create Employee model class with Lombok @Data (id, name, gender, birthday, phone, email, orgName, hireDate, status)
- [x] 1.2 Create EmployeeListReq model class (pageNum, pageSize, name, orgName)
- [x] 1.3 Create EmployeeManageService interface with listEmployees method
- [x] 1.4 Create EmployeeManageServiceImpl using ApiService and AsyncExecutor
- [x] 1.5 Add Guice binding in AppModule: EmployeeManageService → EmployeeManageServiceImpl

## 2. ViewModel

- [x] 2.1 Create EmployeeListViewModel extending ViewModelBase with ObservableList<Employee>, pagination state, search conditions
- [x] 2.2 Implement loadEmployees() method calling EmployeeManageService.listEmployees
- [x] 2.3 Add search by name support in ViewModel
- [x] 2.4 Add pagination navigation (next/previous page) in ViewModel

## 3. View (FXML & Controller)

- [x] 3.1 Create employee_list.fxml with search bar, TableView, and pagination controls
- [x] 3.2 Create EmployeeListController with TableView column bindings (name, gender, phone, email, orgName, hireDate, status)
- [x] 3.3 Implement search button action triggering ViewModel.loadEmployees()
- [x] 3.4 Implement pagination controls (previous/next buttons, page indicator)
- [x] 3.5 Implement status column display (1=在职/Active, 0=离职/Inactive)
- [x] 3.6 Add i18n support for column headers and labels via I18nService

## 4. Navigation & Configuration

- [x] 4.1 Update navigation.yaml: rename user management node to employee management, change route to /system/employee/list, FXML to employee_list
- [x] 4.2 Add i18n keys for employee management in messages_zh_CN.properties and messages_en_US.properties

## 5. Verification

- [x] 5.1 Run mvn compile to verify no compilation errors
