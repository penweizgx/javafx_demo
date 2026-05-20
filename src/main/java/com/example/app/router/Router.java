package com.example.app.router;

import com.example.app.AppContext;
import com.example.app.ViewManager;
import com.example.app.exception.ExceptionHandler;
import com.example.app.guard.GuardResult;
import com.example.app.guard.NavigationGuard;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationIcons;
import com.example.app.navigation.RouteChangeEvent;
import com.example.app.navigation.ParamReceiver;
import com.example.app.navigation.RouteParams;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Router {

    private final RouteRegistry registry;
    private TabPane tabPane;
    private final HistoryManager historyManager;
    private final List<NavigationGuard> globalGuards = new ArrayList<>();

    private final Map<String, Tab> tabByPath = new HashMap<>();

    private RouteHistory current;

    public Router(RouteRegistry registry, TabPane tabPane) {
        this.registry = registry;
        this.tabPane = tabPane;
        this.historyManager = new HistoryManager();
        if (tabPane != null) {
            setupTabListener();
        }
    }

    public Router(RouteRegistry registry) {
        this(registry, null);
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        setupTabListener();
    }

    public void addGlobalGuard(NavigationGuard guard) {
        globalGuards.add(guard);
    }

    public void removeGlobalGuard(NavigationGuard guard) {
        globalGuards.remove(guard);
    }

    public void navigate(String path) {
        navigate(path, RouteParams.empty());
    }

    public void navigate(String path, RouteParams params) {
        RouteMatch match = registry.match(path);
        if (match == null) {
            log.warn("Route not found: {}", path);
            return;
        }

        RouteParams merged = new RouteParams();
        merged.addAll(match.getPathParams());
        if (params != null) {
            merged.getData().putAll(params.getData());
            if (params.getTabId() != null) merged.tabId(params.getTabId());
            if (params.getSubPagePath() != null) merged.subPage(params.getSubPagePath());
        }

        GuardResult guardResult = executeGuards(path, merged);
        if (!guardResult.isAllowed()) {
            handleGuardResult(guardResult);
            return;
        }

        doNavigate(path, match.getFxmlPath(), merged, match);
    }

    private GuardResult executeGuards(String path, RouteParams params) {
        for (NavigationGuard guard : globalGuards) {
            GuardResult result = guard.beforeEach(path, params);
            if (!result.isAllowed()) {
                return result;
            }
        }
        return GuardResult.allow();
    }

    private void doNavigate(String path, String fxmlPath, RouteParams params, RouteMatch match) {
        try {
            if (tabPane == null) {
                log.warn("TabPane not set, cannot navigate to: {}", path);
                return;
            }

            Tab existingTab = tabByPath.get(path);
            if (existingTab != null) {
                tabPane.getSelectionModel().select(existingTab);
                return;
            }

            Parent page = loadPage(fxmlPath, params);

            Tab tab = new Tab();
            tab.setText(getTabTitle(path, params, match));
            if (match.getIcon() != null) {
                FontIcon icon = NavigationIcons.createIcon(match.getIcon(), 14);
                if (icon != null) {
                    tab.setGraphic(icon);
                }
            }
            tab.setContent(page);
            tab.setClosable(isClosable(path));
            tab.setUserData(new RouteHistory(path, params, tab));

            tab.setOnClosed(e -> {
                tabByPath.remove(path);
                historyManager.onTabClosed(tab);
                EventBus.getInstance().publish(RouteChangeEvent.tabClose(path, tab));
            });

            tabByPath.put(path, tab);

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            RouteHistory history = new RouteHistory(path, params, tab);
            historyManager.push(history);
            current = history;

            EventBus.getInstance().publish(RouteChangeEvent.navigate(path, params, tab));

        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to navigate to: " + path);
        }
    }

    private Parent loadPage(String fxmlPath, RouteParams params) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(cls -> {
            try {
                return AppContext.get().getService(cls);
            } catch (Exception e) {
                try {
                    return cls.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Parent page = loader.load();

        Object controller = loader.getController();
        if (controller instanceof ParamReceiver receiver && params != null) {
            receiver.receiveParams(params);
        }

        return page;
    }

    private String getTabTitle(String path, RouteParams params, RouteMatch match) {
        if (match != null && match.getLabel() != null) {
            return match.getLabel();
        }
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : path;
    }

    private boolean isClosable(String path) {
        return !"/home".equals(path);
    }

    private void setupTabListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                RouteHistory entry = (RouteHistory) newTab.getUserData();
                if (entry != null) {
                    current = entry;
                    EventBus.getInstance().publish(RouteChangeEvent.tabSwitch(entry.getPath(), newTab));
                }
            }
        });
    }

    public void back() {
        RouteHistory prev = historyManager.back();
        if (prev != null) {
            Tab tab = prev.getTab();
            if (tab != null && tabPane.getTabs().contains(tab)) {
                tabPane.getSelectionModel().select(tab);
            } else {
                navigate(prev.getPath(), prev.getParams());
            }
            EventBus.getInstance().publish(RouteChangeEvent.back(prev.getPath(), prev.getTab()));
        }
    }

    public void forward() {
        RouteHistory next = historyManager.forward();
        if (next != null) {
            Tab tab = next.getTab();
            if (tab != null && tabPane.getTabs().contains(tab)) {
                tabPane.getSelectionModel().select(tab);
            } else {
                navigate(next.getPath(), next.getParams());
            }
            EventBus.getInstance().publish(RouteChangeEvent.forward(next.getPath(), next.getTab()));
        }
    }

    public boolean canGoBack() {
        return historyManager.canGoBack();
    }

    public boolean canGoForward() {
        return historyManager.canGoForward();
    }

    public String getCurrentPath() {
        return current != null ? current.getPath() : null;
    }

    public RouteParams getCurrentParams() {
        return current != null ? current.getParams() : null;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void handleGuardResult(GuardResult result) {
        if (result.getRedirectPath() != null) {
            navigate(result.getRedirectPath());
        } else if (result.getMessage() != null) {
            log.info("Navigation blocked: {}", result.getMessage());
        }
    }

}
