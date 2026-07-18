## ADDED Requirements

### Requirement: Monthly attendance refund list
The system SHALL display a refund list for a selected month and class, fetched from `GET /finance/listMonthAttendCarryOver` with schId, clazzId, month, and onlyrefund parameters. Each row SHALL display: student name, total leave days, refund days, refund items, and anomaly information. The "only show refund items" checkbox SHALL be checked by default.

#### Scenario: Load refund list for selected month and class
- **WHEN** user selects a month and class and the page loads
- **THEN** system calls `GET /finance/listMonthAttendCarryOver` with the selected parameters and displays the refund list

#### Scenario: Toggle only-refund filter
- **WHEN** user unchecks the "only show refund items" checkbox
- **THEN** system calls the API with onlyrefund=false and displays all records including those without refund

#### Scenario: Display anomaly information
- **WHEN** a student's refund record has anomaly data from the backend
- **THEN** system displays the anomaly information in the "anomaly" column as returned by the API

### Requirement: Confirm monthly attendance carry-over
The system SHALL provide a "Confirm" action that calls `POST /finance/confirmMonthAttendCarryOver/{year}/{month}` with a list of `MonthAttendCarryOverVO` records to finalize the refund carry-over amounts.

#### Scenario: Confirm carry-over for a month
- **WHEN** user clicks the "Confirm" button after reviewing the refund list
- **THEN** system calls the confirm API with the year, month, and the list of carry-over records

### Requirement: Manual attendance refund
The system SHALL provide a "Refund" action for individual students that calls `POST /finance/attendCarryOverBill` with `StudentMonthAttendDTO` and a remark to generate a refund based on attendance data.

#### Scenario: Generate manual refund
- **WHEN** user clicks "Refund" on a student row and provides a remark
- **THEN** system calls `POST /finance/attendCarryOverBill` with the student's attendance data and remark

### Requirement: Refund standard configuration
The system SHALL allow viewing and setting the refund standard for a fee subject via `GET/POST /finance/attendCarryOverConfig?subjectId={id}`. The refund standard includes an amount and a time unit.

#### Scenario: View refund standard
- **WHEN** user navigates to the refund standard for a subject
- **THEN** system displays the current refund amount and unit from the API

#### Scenario: Set refund standard
- **WHEN** user enters a new refund amount and submits
- **THEN** system calls `POST /finance/attendCarryOverConfig?subjectId={id}&amount={amount}` and confirms the update
