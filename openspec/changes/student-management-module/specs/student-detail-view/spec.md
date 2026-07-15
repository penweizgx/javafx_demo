## ADDED Requirements

### Requirement: Display student detail page

The system SHALL display a student's complete information in grouped sections on a single page.

**Data source:** `GET /student/detail/{id}`

#### Scenario: Detail page loads

- **WHEN** user navigates to `/student/detail/:id`
- **THEN** the system SHALL fetch student detail from `/student/detail/{id}`
- **THEN** the page SHALL show the student's name and status badge at the top
- **THEN** the page SHALL render grouped information sections below

### Requirement: Basic info section

The basic info section SHALL display: name, gender, birthday, phone, address, contact person(s).

#### Scenario: Basic info rendered

- **WHEN** student detail is loaded and has parent/contact data
- **THEN** the basic info section SHALL show each contact person's name, relationship, and phone

### Requirement: Attendance info section

The attendance section SHALL display the student's monthly attendance summary.

**Data source:** `GET /attend/listMonthAttend/{studentId}?month=<currentMonth>`

#### Scenario: Attendance rendered

- **WHEN** student detail is loaded
- **THEN** the system SHALL also fetch attendance data for the current month
- **THEN** the attendance section SHALL show: attendance days, leave days, absence days

### Requirement: Finance info section

The finance section SHALL display the student's account balance.

**Data source:** `GET /finance/account/{studentId}`

#### Scenario: Finance rendered

- **WHEN** student detail is loaded
- **THEN** the system SHALL also fetch financial account data
- **THEN** the finance section SHALL show: account balance

### Requirement: Class history section

The class history section SHALL display the student's class transfer and graduation history.

**Data source:** From `StudentVO.clazzs[]`

#### Scenario: Class history rendered

- **WHEN** student detail is loaded and has class history data
- **THEN** the class history section SHALL display each entry with date, from class, to class

### Requirement: Page has back button

The detail page SHALL have a "返回" button to go back to the previous page.

#### Scenario: Back navigation

- **WHEN** user clicks "返回"
- **THEN** the system SHALL navigate back to the previous page (either class student list or search results)
