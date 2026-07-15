## ADDED Requirements

### Requirement: Global student search on class cards page

The system SHALL provide a search input at the top of the class cards page that searches all students across the entire school.

**Data source:** `GET /student/listByCondition`

#### Scenario: Search returns results

- **WHEN** user types a keyword and presses Enter or clicks Search
- **THEN** the system SHALL call `/student/listByCondition` with the keyword
- **THEN** the class cards view SHALL be replaced by a paginated student list showing results
- **THEN** the list SHALL display columns: name, class name, phone, status

#### Scenario: Search with empty keyword

- **WHEN** user clicks Search with an empty input
- **THEN** the system SHALL NOT make an API call
- **THEN** the class cards SHALL remain visible

#### Scenario: Clear search restores cards

- **WHEN** user clears the search input and submits
- **THEN** the system SHALL restore the class cards view

#### Scenario: Search result pagination

- **WHEN** search returns more results than the page size
- **THEN** the system SHALL paginate results with previous/next buttons
- **THEN** the page info SHALL show current page and total count
