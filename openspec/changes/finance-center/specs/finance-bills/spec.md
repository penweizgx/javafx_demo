## ADDED Requirements

### Requirement: Paginated bill list with filters
The system SHALL display a paginated list of charge records fetched from `POST /finance/bills/list` with `FilterBillsPage` body. Filters SHALL include: student keyword search, class dropdown, bill type dropdown (BASE/CHARGE/CARRY_OVER/REFUND/DEPOSIT), date period, and an option to filter out invalid records. Each row SHALL display: business date, student name.class, bill type, receivable amount, real amount, balance deduction, item details, payment info, operation time, and a "View" action.

#### Scenario: Load bills with default filters
- **WHEN** user navigates to the bills page
- **THEN** system calls `POST /finance/bills/list` with default pagination and displays the results

#### Scenario: Filter by bill type
- **WHEN** user selects a bill type from the dropdown and clicks query
- **THEN** system includes the billType in the FilterBillsPage request and refreshes the list

#### Scenario: Filter by date period
- **WHEN** user selects a start and end date
- **THEN** system includes the period in the request and refreshes the list

#### Scenario: Paginate through results
- **WHEN** user clicks next page
- **THEN** system increments the current page in FilterBillsPage and refreshes the list

### Requirement: View bill detail
The system SHALL provide a "View" action for each bill row that fetches detail from `GET /finance/charge/{id}` and displays the `ChargeBillVO` including the charge bill with subject items, payment channels, and any associated carry-over bills.

#### Scenario: View charge bill detail
- **WHEN** user clicks "View" on a CHARGE type bill
- **THEN** system opens a detail view showing the bill's subject items, payment amounts, and deduction details

#### Scenario: View carry-over bill detail
- **WHEN** user clicks "View" on a CARRY_OVER type bill
- **THEN** system opens a detail view showing the carry-over items with subject names and amounts

### Requirement: Invalidate charge bill
The system SHALL provide an "Invalidate" action for valid charge bills that calls `POST /finance/invalidCharge` with chargeBillId and a remark. After invalidation, the bill SHALL be marked as invalid in the list.

#### Scenario: Invalidate a charge bill
- **WHEN** user clicks "Invalidate" on a charge bill and provides a remark
- **THEN** system calls `POST /finance/invalidCharge?chargeBillId={id}&remark={remark}` and refreshes the list

### Requirement: Invalidate carry-over bill
The system SHALL provide an "Invalidate" action for valid carry-over bills that calls `POST /finance/invalidCarry` with billId and a remark.

#### Scenario: Invalidate a carry-over bill
- **WHEN** user clicks "Invalidate" on a carry-over bill and provides a remark
- **THEN** system calls `POST /finance/invalidCarry?billId={id}&remark={remark}` and refreshes the list

### Requirement: Export charge bills
The system SHALL provide an "Export" button that downloads charge bill data via `POST /finance/exportChargeBill` with the current filter criteria.

#### Scenario: Export with current filters
- **WHEN** user clicks the export button
- **THEN** system downloads the filtered charge bill data as a file
