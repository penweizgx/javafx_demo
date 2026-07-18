## ADDED Requirements

### Requirement: Fee subject list with expandable fee scales
The system SHALL display a list of fee subjects fetched from `GET /finance/subject/list` returning `List<SubjectWithFeeScaleDTO>`. Each row SHALL display: expand button, subject name, type (PERIOD/ONCE), status (enabled/disabled), remind period, fee scale count, refund standard, and action buttons. Expanding a row SHALL reveal a sub-table of `FeeScaleDTO` items with: name, standard amount + unit, usage count, remark, and actions.

#### Scenario: Load fee subjects
- **WHEN** user navigates to the subject page
- **THEN** system calls `GET /finance/subject/list` and displays all fee subjects in a table

#### Scenario: Expand to view fee scales
- **WHEN** user clicks the expand button on a subject row
- **THEN** system reveals the sub-table showing all fee scales for that subject

#### Scenario: Filter by type
- **WHEN** user selects PERIOD or ONCE filter
- **THEN** system filters the displayed subjects by the selected type

### Requirement: Add fee subject
The system SHALL provide an "Add Subject" button that opens a dialog with fields: name (required), type (PERIOD/ONCE radio), remind period (days, number input), and remark. On submit, the system SHALL call `POST /finance/subject/add` with `SubjectDTO`.

#### Scenario: Add a new period subject
- **WHEN** user fills in the form and clicks confirm
- **THEN** system calls `POST /finance/subject/add` and refreshes the subject list

### Requirement: Edit fee subject
The system SHALL provide an "Edit" action on each subject row that opens a dialog pre-filled with the subject's current data. On submit, the system SHALL call `POST /finance/subject/edit` with `SubjectDTO`.

#### Scenario: Edit subject name and remind period
- **WHEN** user modifies the name and remind period and clicks confirm
- **THEN** system calls `POST /finance/subject/edit` and refreshes the subject list

### Requirement: Disable fee subject
The system SHALL provide a "Disable" action on each enabled subject row that calls `POST /finance/subject/disable?id={id}`.

#### Scenario: Disable a subject
- **WHEN** user clicks "Disable" on a subject
- **THEN** system calls the disable API and updates the subject's status in the list

### Requirement: Remove fee subject
The system SHALL provide a "Remove" action on each subject row that calls `POST /finance/subject/remove?id={id}`.

#### Scenario: Remove a subject
- **WHEN** user clicks "Remove" and confirms
- **THEN** system calls the remove API and removes the subject from the list

### Requirement: Set refund standard for subject
The system SHALL provide a "Refund Setting" action on each subject row that opens a dialog with amount and unit fields. Data SHALL be loaded from `GET /finance/attendCarryOverConfig?subjectId={id}` and saved via `POST /finance/attendCarryOverConfig?subjectId={id}&amount={amount}`.

#### Scenario: Set refund standard
- **WHEN** user enters an amount and clicks confirm
- **THEN** system calls the save API and updates the refund standard display in the subject row

### Requirement: Add fee scale to subject
The system SHALL provide an "Add Scale" action on each subject row that opens a dialog with fields: standard amount (required), unit (required, dropdown: ONCE/M/Q/T/HY/FY/Y), name (optional), and remark (optional). On submit, the system SHALL call `POST /finance/feesScale/add?subjectId={id}&standardAmount={amount}&unit={unit}`.

#### Scenario: Add a new fee scale
- **WHEN** user fills in the amount and unit and clicks confirm
- **THEN** system calls the add API and refreshes the expanded sub-table

### Requirement: Rename fee scale
The system SHALL provide a "Rename" action on each fee scale row that calls `POST /finance/feesScale/changeName?id={id}&name={name}`.

#### Scenario: Rename a fee scale
- **WHEN** user enters a new name and clicks confirm
- **THEN** system calls the rename API and updates the name in the sub-table

### Requirement: Disable fee scale
The system SHALL provide a "Disable" action on each enabled fee scale row that calls `POST /finance/feeScale/disable?id={id}`.

#### Scenario: Disable a fee scale
- **WHEN** user clicks "Disable" on a fee scale
- **THEN** system calls the disable API and marks the scale as disabled

### Requirement: Batch charge by fee scale
The system SHALL provide a "Batch Charge" action on each fee scale row that initiates batch charging via `POST /finance/batchCharge` with `BatchChargeDTO` containing feeScaleId, period, bizDate, and student items.

#### Scenario: Initiate batch charge
- **WHEN** user clicks "Batch Charge" on a fee scale
- **THEN** system opens a batch charge dialog where user can select students and confirm
