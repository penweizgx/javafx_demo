## ADDED Requirements

### Requirement: Payment method management
The system SHALL display a list of payment methods fetched from `GET /finance/payChancels/list?schId={id}`. Each row SHALL show: payment method name and hidden status. The system SHALL provide "Add" and "Remove" actions.

#### Scenario: Load payment methods
- **WHEN** user navigates to the config page
- **THEN** system displays the payment method list from the API

#### Scenario: Add payment method
- **WHEN** user clicks "Add", enters a name, and confirms
- **THEN** system calls `POST /finance/payChancel/add?schId={id}&name={name}` and refreshes the list

#### Scenario: Remove payment method
- **WHEN** user clicks "Remove" on a payment method and confirms
- **THEN** system calls `POST /finance/payChancel/remove?schId={id}&id={id}` and refreshes the list

### Requirement: Bill number rule configuration
The system SHALL display the current bill number rule fetched from `GET /finance/billNumberRule?schId={id}` showing: length, prefix, start, and serial number. The system SHALL allow editing and saving via `POST /finance/billNumberRule/save` with `BillNumberRule`.

#### Scenario: View bill number rule
- **WHEN** user navigates to the config page
- **THEN** system displays the current bill number rule

#### Scenario: Modify bill number rule
- **WHEN** user edits the rule fields and clicks save
- **THEN** system calls `POST /finance/billNumberRule/save` and confirms the update

### Requirement: Remind period configuration
The system SHALL display and allow editing the remind period (days before fee expiry for system reminder) from `FinanceConfig.remind`. Data SHALL be loaded from `GET /finance/config?schId={id}` and saved via `POST /finance/config?schId={id}`.

#### Scenario: View remind period
- **WHEN** user navigates to the config page
- **THEN** system displays the current remind period value

#### Scenario: Update remind period
- **WHEN** user modifies the remind period and clicks save
- **THEN** system calls `POST /finance/config` with the updated FinanceConfig and confirms

### Requirement: Attendance refund threshold configuration
The system SHALL display and allow editing the continuous absence threshold (days) from `FinanceConfig.continuous`. When a student's continuous absence exceeds this threshold, a refund is triggered. The system SHALL also provide a "Validate Account" button that calls `GET /finance/validate` to run account validation checks.

#### Scenario: View attendance refund threshold
- **WHEN** user navigates to the config page
- **THEN** system displays the current continuous absence threshold value

#### Scenario: Update attendance refund threshold
- **WHEN** user modifies the threshold and clicks save
- **THEN** system calls `POST /finance/config` with the updated FinanceConfig and confirms

#### Scenario: Validate accounts
- **WHEN** user clicks the "Validate Account" button
- **THEN** system calls `GET /finance/validate` and if the response contains errors, displays them in an alert dialog; if no errors, displays a success message
