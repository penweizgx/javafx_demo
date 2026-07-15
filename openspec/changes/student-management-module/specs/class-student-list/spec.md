## ADDED Requirements

### Requirement: Display paginated student list for a class

The system SHALL display a paginated table of all students belonging to a specific class.

**Data source:** `GET /student/listWithClazz/{clazzId}`

#### Scenario: Student list loads

- **WHEN** user navigates to `/student/class/:clazzId`
- **THEN** the system SHALL fetch students from `/student/listWithClazz/{clazzId}`
- **THEN** the page title SHALL show the class name
- **THEN** students SHALL be displayed in a TableView

#### Scenario: Table columns

- **WHEN** the student list is displayed
- **THEN** it SHALL show columns: 姓名, 性别, 手机号, 状态, 操作(查看)

### Requirement: Student list header actions

The page SHALL have a "返回" button and a "新增学生" button.

#### Scenario: Back button

- **WHEN** user clicks "返回"
- **THEN** the system SHALL navigate back to `/student/classes`

#### Scenario: Add student button

- **WHEN** user clicks "新增学生"
- **THEN** the system SHALL open the create student modal

### Requirement: Click student to view detail

The system SHALL navigate to the student detail page when a student row is clicked.

#### Scenario: Double-click student row

- **WHEN** user double-clicks a student row or clicks "查看"
- **THEN** the system SHALL navigate to `/student/detail/:id` with the student's data
