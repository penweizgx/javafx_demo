## ADDED Requirements

### Requirement: Open create student modal

The system SHALL open a modal dialog for creating a new student when the "新增学生" button is clicked.

#### Scenario: Modal opens

- **WHEN** user clicks "新增学生" on the class student list page
- **THEN** a modal dialog SHALL appear with the title "新增学生"

### Requirement: Create student form fields

The modal SHALL contain input fields for: name, gender (dropdown), birthday (date picker), phone, address, class (dropdown), contact person name, contact person relationship (dropdown).

#### Scenario: Form rendered

- **WHEN** the modal is opened
- **THEN** the form SHALL show all required input fields
- **THEN** the gender dropdown SHALL contain options: 男, 女
- **THEN** the class dropdown SHALL be populated with active classes from `/org/clazz/listActive`
- **THEN** the contact relationship dropdown SHALL contain options: 父亲, 母亲, 祖父, 祖母, 其他

### Requirement: Submit new student

The system SHALL create a new student via API when the form is submitted.

**Data source:** `POST /student/create` with `StudentFO` body

#### Scenario: Create student successfully

- **WHEN** user fills in all required fields and clicks "保存"
- **THEN** the system SHALL call `POST /student/create`
- **THEN** on success, the modal SHALL close
- **THEN** the student list SHALL refresh to show the new student

#### Scenario: Create student with validation error

- **WHEN** user submits the form without filling required fields (name, phone)
- **THEN** the system SHALL highlight the missing fields
- **THEN** the modal SHALL remain open

#### Scenario: Cancel create

- **WHEN** user clicks "取消" in the modal
- **THEN** the modal SHALL close without saving
