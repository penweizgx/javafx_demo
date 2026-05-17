## ADDED Requirements

### Requirement: Employee detail modal display
The system SHALL display employee details in a modal dialog when the user clicks the "View" button in the user list.

#### Scenario: Display modal on view button click
- **WHEN** user clicks the "View" button for a user in the user list
- **THEN** system SHALL display a modal dialog showing the selected user's details
- **AND** the modal SHALL overlay the current page without navigation

#### Scenario: Modal content layout
- **WHEN** the employee detail modal is displayed
- **THEN** the modal SHALL show the following information:
  - User name
  - Email address
  - Phone number
  - Department/Organization
  - Role
  - Status (with badge styling)
  - Create time
- **AND** the information SHALL be displayed in a readable card layout

### Requirement: Modal actions
The system SHALL provide action buttons in the employee detail modal.

#### Scenario: Close modal action
- **WHEN** user clicks the "Close" button in the modal
- **THEN** the modal SHALL close
- **AND** the user SHALL return to the user list page

#### Scenario: Navigate to detail page action
- **WHEN** user clicks the "View Details" button in the modal
- **THEN** the modal SHALL close
- **AND** the system SHALL navigate to the full user detail page
- **AND** the user detail page SHALL display the selected user's information

### Requirement: Modal styling
The employee detail modal SHALL use AtlantaFX styling for consistency.

#### Scenario: Card-based layout
- **WHEN** the modal is displayed
- **THEN** the content SHALL be wrapped in an AtlantaFX Card component
- **AND** the modal SHALL have appropriate padding and spacing

#### Scenario: Status badge styling
- **WHEN** displaying user status in the modal
- **THEN** the status SHALL be shown as a badge with appropriate color:
  - "active" → success/green badge with text "正常"
  - "inactive" → danger/red badge with text "停用"
  - "pending" → warning/yellow badge with text "待审核"

### Requirement: Modal size and positioning
The employee detail modal SHALL have appropriate size and positioning.

#### Scenario: Modal dimensions
- **WHEN** the modal is displayed
- **THEN** the modal SHALL have a maximum width of 500px
- **AND** the modal SHALL be centered on the screen
- **AND** the modal SHALL have a semi-transparent background overlay
