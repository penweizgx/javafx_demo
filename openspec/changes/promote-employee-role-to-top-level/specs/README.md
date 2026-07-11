# Navigation Menu Restructuring Specs

This directory contains specifications for promoting employee and role management to top-level menu items.

## Modified Capabilities

### navigation
**Change**: Menu hierarchy adjustment only. No functional behavior changes.

**Details**: 
- `employee-mgmt` moved from `system.children` to top-level `navigation`
- `role-mgmt` moved from `system.children` to top-level `navigation`
- All route paths, FXML mappings, and child nodes remain unchanged
