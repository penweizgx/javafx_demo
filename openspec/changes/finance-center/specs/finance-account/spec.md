## ADDED Requirements

### Requirement: Finance dashboard brief
The system SHALL display a dashboard at the top of the student account page showing 4 summary cards: overdue (expirePerson/expireSubject), reminder (remindPerson/remindSubject), deposit (depositNum/depositAmount), and free (freePerson). Data SHALL be fetched from `GET /finance/countBrief`.

#### Scenario: Dashboard loads successfully
- **WHEN** user navigates to the student account page
- **THEN** system calls `GET /finance/countBrief?schId={currentSchId}` and displays 4 cards with the returned data

#### Scenario: Dashboard data unavailable
- **WHEN** the countBrief API returns an error
- **THEN** system displays placeholder values (0) on the cards and shows an error message

### Requirement: Student account list with filters
The system SHALL display a paginated student account list supporting filters: student name/pinyin search, class dropdown (from `GET /org/clazz/listActive`), and student status filter. Data SHALL be fetched from `GET /finance/listByCondition` using `StudentQO` parameters. Each row SHALL display: name+gender, class, enrollment date, account balance, and associated fee items with standards.

#### Scenario: List loads for a selected class
- **WHEN** user selects a class from the dropdown
- **THEN** system calls `GET /finance/listByCondition` with clazzId and displays the filtered student list

#### Scenario: Search by student name
- **WHEN** user enters a keyword and clicks query
- **THEN** system calls `GET /finance/listByCondition` with the keyword parameter and displays matching students

#### Scenario: Reset filters
- **WHEN** user clicks the reset button
- **THEN** system clears all filter fields and reloads the default student list

### Requirement: Export student account by class
The system SHALL provide an "Export" button that downloads student account data for the currently selected class via `GET /finance/exportFinanceAccount?clazzId={clazzId}`.

#### Scenario: Export for selected class
- **WHEN** user clicks the export button with a class selected
- **THEN** system downloads the Excel file for that class

### Requirement: Import charge data
The system SHALL provide an "Import" button that opens a dialog for uploading a charge data file via `POST /finance/importCharge`. After upload, the system SHALL display the import result (total/success/failed counts and error details).

#### Scenario: Successful import
- **WHEN** user uploads a valid file
- **THEN** system displays the import result showing total, success, and failed counts

#### Scenario: Import with errors
- **WHEN** some rows in the uploaded file have errors
- **THEN** system displays the error details for each failed row, including student name, fee scale name, and error messages

### Requirement: View student account detail
The system SHALL provide a "View" action for each student that opens a detail dialog showing: basic student info, account summary (total paid/refund/balance), fee items table (name/standard amount/special amount/expire date), and charge history table (time/amount/real amount/balance/type/date/details). Data SHALL be fetched from `GET /finance/account/{studentId}` and `GET /finance/chargeBills/{studentId}`.

#### Scenario: Open account detail
- **WHEN** user clicks the "View" action on a student row
- **THEN** system opens a dialog displaying the student's complete account information

#### Scenario: View charge history record details
- **WHEN** user clicks "View" on a charge history row
- **THEN** system expands the row to show item-level breakdown (subject items with amounts and payment channels)

### Requirement: Charge student
The system SHALL provide a "Charge" action that opens a charge dialog. The dialog SHALL load charge form data from `GET /finance/buildChargeForm/{studentId}`, display selectable fee items (with period/amount), payment method selection, and payer selection. On submit, the system SHALL call `POST /finance/charge` with `ChargeBillDTO`.

#### Scenario: Open charge dialog
- **WHEN** user clicks the "Charge" action on a student row
- **THEN** system opens the charge dialog loaded with the student's available fee items, payment methods, payer list, and current balance

#### Scenario: Select fee items and submit charge
- **WHEN** user selects fee items, fills in amounts/periods, selects payment methods, and clicks confirm
- **THEN** system calls `POST /finance/charge` and on success refreshes the student account data

#### Scenario: Balance deduction
- **WHEN** the student has a positive balance and the user enables balance deduction
- **THEN** system allows specifying the deduction amount up to the available balance, and includes it in the `balancesAmount` field of each SubjectItem

#### Scenario: Multiple payment methods
- **WHEN** user fills in amounts for multiple payment channels
- **THEN** system submits all non-zero payment channels in the `payChancels` array

### Requirement: Set student fee standards
The system SHALL provide a "Set Standards" action that opens a dialog with a TabPane organized by fee subject. Each tab displays fee scales as checkboxes. On submit, the system SHALL call `POST /finance/feeScale/bind` with the student ID and selected fee scale IDs.

#### Scenario: Open set standards dialog
- **WHEN** user clicks the "Set Standards" action
- **THEN** system opens a dialog with tabs for each fee subject, each containing its fee scales as checkboxes

#### Scenario: Bind selected standards
- **WHEN** user selects fee scales across tabs and clicks confirm
- **THEN** system calls `POST /finance/feeScale/bind` with the studentId and selected ids[]

### Requirement: Archive student (close account)
The system SHALL provide an "Archive" action in the student account detail dialog that calls `POST /finance/close?studentId={id}` to mark the student as archived.

#### Scenario: Archive student
- **WHEN** user clicks the "Archive" button in the account detail dialog
- **THEN** system calls the close API and on success refreshes the student list

### Requirement: PeriodUnit display
The system SHALL display fee period units in Chinese using the mapping: ONCE→次, M→月, Q→季度, T→学期, HY→半年, FY→年, Y→年. Both FY and Y SHALL display identically as "年".

#### Scenario: Display fee item with period unit
- **WHEN** a fee item has unit "HY" and standardAmount 16880
- **THEN** system displays "16880/半年"
