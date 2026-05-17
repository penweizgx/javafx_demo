## ADDED Requirements

### Requirement: Navigation node visibility control
The system SHALL allow configuration of whether a navigation node appears in the navigation bar through the `showInNav` property.

#### Scenario: Node with showInNav true appears in navigation
- **WHEN** a navigation node has `showInNav: true` or no `showInNav` property (default)
- **THEN** the node SHALL be rendered in the navigation bar

#### Scenario: Node with showInNav false hidden from navigation
- **WHEN** a navigation node has `showInNav: false`
- **THEN** the node SHALL NOT be rendered in the navigation bar
- **AND** the node SHALL still be available for routing and parameter matching

### Requirement: Page-internal elements distinguished from global navigation
The system SHALL distinguish between global navigation items and page-internal elements based on configuration.

#### Scenario: Tab items marked as page-internal
- **WHEN** a navigation node represents a page-internal tab (has `tabId` property)
- **THEN** the node SHOULD be configured with `showInNav: false` by default
- **AND** the node SHALL still participate in route parameter resolution

#### Scenario: Direct navigation items remain visible
- **WHEN** a navigation node has a `path` property and no `tabId`
- **THEN** the node SHALL be visible in the navigation bar by default

### Requirement: Navigation hierarchy levels
The system SHALL support hierarchical navigation with proper indentation and grouping.

#### Scenario: Top-level navigation group
- **WHEN** a navigation node has no parent and has children
- **THEN** the node SHALL be rendered as a collapsible group in the navigation bar

#### Scenario: Sub-level navigation items
- **WHEN** a navigation node has a parent node
- **THEN** the node SHALL be indented according to its depth level
- **AND** the node SHALL be hidden when its parent group is collapsed

#### Scenario: Deeply nested items hidden from main navigation
- **WHEN** a navigation node is nested more than 2 levels deep AND has `tabId` or `showInNav: false`
- **THEN** the node SHALL NOT appear in the navigation bar
- **AND** the node SHALL be accessible through page-internal navigation

### Requirement: Navigation configuration schema
The navigation configuration file SHALL support the `showInNav` property for all nodes.

#### Scenario: YAML configuration with showInNav
- **WHEN** a navigation node is defined in `navigation.yaml`
- **THEN** the node MAY specify `showInNav: false` to hide it from the navigation bar
- **AND** the default value SHALL be `true` if not specified

#### Scenario: Backward compatibility
- **WHEN** a navigation node does not specify `showInNav`
- **THEN** the system SHALL treat it as `showInNav: true`
- **AND** existing navigation configurations SHALL continue to work without modification
