## ADDED Requirements

### Requirement: Toggle edit mode on detail page

The system SHALL allow switching the detail page between view mode and edit mode.

#### Scenario: Enter edit mode

- **WHEN** user clicks the "编辑" button on the detail page
- **THEN** all editable fields SHALL change from Label display to TextField/ComboBox input controls
- **THEN** the "编辑" button SHALL be replaced with "保存" and "取消" buttons

#### Scenario: Cancel edit restores original values

- **WHEN** user clicks "取消" during edit mode
- **THEN** all fields SHALL revert to their original values
- **THEN** the page SHALL return to view mode

### Requirement: Save edited student information

The system SHALL save edited student information via API.

**Data source:** `POST /student/change` with `StudentFO` body

#### Scenario: Save edit successfully

- **WHEN** user clicks "保存" after modifying fields
- **THEN** the system SHALL call `POST /student/change` with the modified data
- **THEN** on success, the detail page SHALL reload with updated data
- **THEN** the page SHALL return to view mode

#### Scenario: Save edit fails

- **WHEN** the save API call fails
- **THEN** the system SHALL display an error message
- **THEN** the page SHALL remain in edit mode

### Requirement: Edit fields cannot change class

The edit mode SHALL NOT include class/class transfer functionality. Class transfer is handled by a separate independent operation.

#### Scenario: Class field read-only

- **WHEN** in edit mode
- **THEN** the class name SHALL remain as display-only text
- **THEN** no class selection control SHALL be shown
