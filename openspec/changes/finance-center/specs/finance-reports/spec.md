## ADDED Requirements

### Requirement: Monthly charge summary report
The system SHALL display a 4-column summary table fetched from `GET /finance/countChargeReport?schId={id}&month={month}`. The response contains 4 objects: charge (receivableAmount/payAmount/balancesAmount/billnumber/studentNum), balances (balancesAmount/billnumber/studentNum), carryOver (amount/billnumber/studentNum), and refund (amount/billnumber/studentNum). The table SHALL display: receivable amount (charge only), real amount (charge only), amount (balances/carryOver/refund), bill count, and student count for each column.

#### Scenario: Load monthly summary
- **WHEN** user selects a month
- **THEN** system calls `GET /finance/countChargeReport` and displays the 4-column summary table

#### Scenario: Month with no data
- **WHEN** the API returns zero values
- **THEN** system displays the table with all values as 0

### Requirement: Fee subject analysis table
The system SHALL display a fee subject analysis table fetched from `GET /finance/sumChargeSubjectReport?schId={id}&month={month}`. Each row SHALL show: subject name, receivable amount, balance deduction amount, real amount, and real/receivable ratio.

#### Scenario: Load subject analysis
- **WHEN** user selects a month
- **THEN** system calls the API and displays the analysis table with calculated ratios

### Requirement: Fee subject analysis chart
The system SHALL display a bar chart for fee subject analysis with a dimension selector: real amount (default), receivable amount, or balance deduction. The chart SHALL use `javafx.scene.chart.BarChart`.

#### Scenario: Display bar chart by real amount
- **WHEN** the subject analysis data loads
- **THEN** system displays a bar chart with real amount as the default dimension

#### Scenario: Switch chart dimension
- **WHEN** user selects a different dimension from the selector
- **THEN** system updates the bar chart to display the selected dimension

### Requirement: Carry-over subject analysis table
The system SHALL display a carry-over subject analysis table fetched from `GET /finance/sumCarryOverSubjectReport?schId={id}&month={month}`. Each row SHALL show: subject name, amount, and percentage of total.

#### Scenario: Load carry-over analysis
- **WHEN** user selects a month
- **THEN** system calls the API and displays the carry-over analysis table with calculated percentages

### Requirement: Carry-over subject analysis chart
The system SHALL display a pie chart for carry-over subject analysis using `javafx.scene.chart.PieChart`.

#### Scenario: Display carry-over pie chart
- **WHEN** the carry-over analysis data loads
- **THEN** system displays a pie chart showing each subject's proportion

### Requirement: Payment method statistics
The system SHALL display payment method statistics fetched from `GET /finance/countPayChancelReport?schId={id}&month={month}`. Each row SHALL show: payment method name and amount. A pie chart SHALL visualize the distribution.

#### Scenario: Load payment method statistics
- **WHEN** user selects a month
- **THEN** system calls the API and displays the payment method table and pie chart
