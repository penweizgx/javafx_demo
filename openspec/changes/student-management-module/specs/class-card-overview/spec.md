## ADDED Requirements

### Requirement: Display class cards with student statistics

The system SHALL display a page of class cards showing each class's basic information and student statistics.

**Data source:** `GET /org/clazz/listWithCount`

#### Scenario: Page loads successfully

- **WHEN** user navigates to `/student/classes`
- **THEN** the system SHALL fetch class list from `/org/clazz/listWithCount`
- **THEN** the system SHALL display each class as a card

### Requirement: Class card content

Each class card SHALL display: class name, teacher name(s), active student count, registered student count, leave student count, vacancy/capacity.

#### Scenario: Card shows correct statistics

- **WHEN** a class card is rendered
- **THEN** it SHALL show `activeNum` as "在读 N人"
- **THEN** it SHALL show `regNum` as "登记 N人"
- **THEN** it SHALL show `leaveNum` as "请假 N人"
- **THEN** it SHALL show `vacancy`/`capacity` as "空位 N/M"

### Requirement: Display today's attendance rate per class

The system SHALL fetch today's attendance data and display attendance rate on each class card.

**Data source:** `GET /attend/countClazzDay?day=<today>`

#### Scenario: Attendance rate displayed on card

- **WHEN** class cards are loaded
- **THEN** the system SHALL also call `/attend/countClazzDay` with today's date
- **THEN** each card SHALL show the attendance percentage for its class
- **THEN** if attendance data is unavailable for a class, SHALL show "-"

#### Scenario: Empty class list

- **WHEN** `/org/clazz/listWithCount` returns an empty array
- **THEN** the system SHALL display a message "暂无班级数据"

### Requirement: Click class card to navigate

The system SHALL navigate to the class student list page when a class card is clicked.

#### Scenario: Click card navigates to student list

- **WHEN** user clicks a class card
- **THEN** the system SHALL navigate to `/student/class/:clazzId`
- **THEN** the clazzId SHALL be passed as a route parameter
