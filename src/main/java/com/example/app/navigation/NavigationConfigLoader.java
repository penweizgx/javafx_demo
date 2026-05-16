package com.example.app.navigation;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class NavigationConfigLoader {

    public static NavigationConfig load(String yamlPath) {
        try {
            Yaml yaml = new Yaml();
            InputStream is = NavigationConfigLoader.class.getResourceAsStream(yamlPath);
            if (is == null) {
                log.warn("Navigation config file not found: {}", yamlPath);
                return new NavigationConfig();
            }

            Map<String, Object> data = yaml.load(is);
            NavigationConfig config = parseConfig(data);
            config.indexNodes();
            return config;

        } catch (Exception e) {
            log.error("Failed to load navigation config: {}", yamlPath, e);
            return new NavigationConfig();
        }
    }

    private static NavigationConfig parseConfig(Map<String, Object> data) {
        NavigationConfig config = new NavigationConfig();

        List<Map<String, Object>> navList = (List<Map<String, Object>>) data.get("navigation");
        if (navList != null) {
            for (Map<String, Object> nav : navList) {
                NavigationNode node = parseNode(nav, 1, null);
                config.getRoots().add(node);
            }
        }

        Map<String, Object> quickAccess = (Map<String, Object>) data.get("quickAccess");
        if (quickAccess != null) {
            config.setQuickAccess(parseQuickAccess(quickAccess));
        }

        return config;
    }

    private static NavigationNode parseNode(Map<String, Object> data, int level, NavigationNode parent) {
        NavigationNode node = new NavigationNode();
        node.setId((String) data.get("id"));
        node.setLabel((String) data.get("label"));
        node.setLabelKey((String) data.get("labelKey"));
        node.setIcon((String) data.get("icon"));
        node.setLevel(level);
        node.setExpanded(Boolean.TRUE.equals(data.get("expanded")));
        node.setParent(parent);

        if (data.containsKey("path")) {
            node.setPath((String) data.get("path"));
            node.setFxml((String) data.get("fxml"));
            node.setLeaf(true);
        }

        if (data.containsKey("tabId")) {
            node.setTabId((String) data.get("tabId"));
            node.setLeaf(true);
        }

        if (data.containsKey("options")) {
            Map<String, Object> opts = (Map<String, Object>) data.get("options");
            node.setOptions(parseOptions(opts));
        }

        List<Map<String, Object>> children = (List<Map<String, Object>>) data.get("children");
        if (children != null) {
            node.setLeaf(false);
            for (Map<String, Object> child : children) {
                node.addChild(parseNode(child, level + 1, node));
            }
        }

        return node;
    }

    private static PageOptions parseOptions(Map<String, Object> data) {
        PageOptions opts = new PageOptions();
        if (data == null) return opts;

        opts.setCacheable(Boolean.TRUE.equals(data.getOrDefault("cacheable", true)));
        opts.setSingleton(Boolean.TRUE.equals(data.get("singleton")));
        opts.setClosable(Boolean.TRUE.equals(data.getOrDefault("closable", true)));
        opts.setSupportsSubPage(Boolean.TRUE.equals(data.get("supportsSubPage")));
        opts.setTitleKey((String) data.get("titleKey"));
        opts.setIcon((String) data.get("icon"));
        opts.setPermission((String) data.get("permission"));

        List<String> roles = (List<String>) data.get("roles");
        if (roles != null) {
            opts.setRoles(roles);
        }

        opts.setValidateParams(Boolean.TRUE.equals(data.get("validateParams")));
        opts.setConfirmLeave(Boolean.TRUE.equals(data.get("confirmLeave")));

        return opts;
    }

    private static NavigationConfig.QuickAccessConfig parseQuickAccess(Map<String, Object> data) {
        NavigationConfig.QuickAccessConfig config = new NavigationConfig.QuickAccessConfig();

        List<Map<String, Object>> header = (List<Map<String, Object>>) data.get("header");
        if (header != null) {
            for (Map<String, Object> item : header) {
                config.getHeader().add(parseQuickAccessItem(item));
            }
        }

        List<Map<String, Object>> dashboard = (List<Map<String, Object>>) data.get("dashboard");
        if (dashboard != null) {
            for (Map<String, Object> item : dashboard) {
                config.getDashboard().add(parseQuickAccessItem(item));
            }
        }

        return config;
    }

    private static NavigationConfig.QuickAccessItem parseQuickAccessItem(Map<String, Object> data) {
        NavigationConfig.QuickAccessItem item = new NavigationConfig.QuickAccessItem();
        item.setId((String) data.get("id"));
        item.setLabel((String) data.get("label"));
        item.setLabelKey((String) data.get("labelKey"));
        item.setIcon((String) data.get("icon"));
        item.setPath((String) data.get("path"));
        item.setShowBadge(Boolean.TRUE.equals(data.get("showBadge")));
        item.setBadgeSource((String) data.get("badgeSource"));

        if (data.containsKey("params")) {
            Map<String, Object> params = (Map<String, Object>) data.get("params");
            item.setParams(RouteParams.of(params));
        }

        return item;
    }
}