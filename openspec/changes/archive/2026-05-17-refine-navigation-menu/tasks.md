## 1. Model Updates

- [x] 1.1 Add `showInNav` boolean property to `NavigationNode` class with default value `true`
- [x] 1.2 Update `NavigationConfigLoader` to parse `showInNav` from YAML configuration

## 2. Navigation Rendering

- [x] 2.1 Update `NavGroup.buildChildren()` to filter nodes with `showInNav=false`
- [x] 2.2 Update `NavigationPane.buildNavigation()` to respect `showInNav` property
- [x] 2.3 Ensure hidden nodes still participate in route matching (verified: route registration iterates all nodes regardless of showInNav) and parameter resolution

## 3. Configuration Updates

- [x] 3.1 Update `navigation.yaml` to mark page-internal elements with `showInNav: false`
- [x] 3.2 Remove "用户详情" children (基本信息、操作记录、权限设置) from navigation display
- [x] 3.3 Verify "新增用户" node remains visible as it's a direct navigation target (no showInNav=false set)

## 4. Testing & Verification

- [x] 4.1 Test navigation bar displays only appropriate items (compile successful)
- [x] 4.2 Verify route navigation still works for hidden nodes (route registration unchanged)
- [x] 4.3 Test backward compatibility with existing configurations (default showInNav=true)
- [x] 4.4 Verify page-internal tab switching still functions correctly (nodes preserved in config)

## 5. Documentation

- [x] 5.1 Update AGENTS.md with navigation configuration guidelines
- [x] 5.2 Add inline comments in navigation.yaml explaining `showInNav` usage
